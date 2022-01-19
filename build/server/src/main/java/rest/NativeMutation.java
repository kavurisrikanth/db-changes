package rest;

import classes.MutateResultStatus;
import d3e.core.CloneContext;
import d3e.core.CurrentUser;
import d3e.core.D3ELogger;
import d3e.core.ListExt;
import d3e.core.TransactionWrapper;
import gqltosql.schema.GraphQLDataFetcher;
import gqltosql.schema.IModelSchema;
import graphql.language.Field;
import helpers.ThingEntityHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import models.AnonymousUser;
import models.Thing;
import models.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import repository.jpa.AnonymousUserRepository;
import repository.jpa.OneTimePasswordRepository;
import repository.jpa.RefModelRepository;
import repository.jpa.ThingRepository;
import repository.jpa.UserRepository;
import repository.jpa.UserSessionRepository;
import security.AppSessionProvider;
import store.EntityHelperService;
import store.EntityMutator;
import store.ValidationFailedException;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("api/native/")
public class NativeMutation extends AbstractQueryService {
  @Autowired private EntityMutator mutator;
  @Autowired private ObjectFactory<EntityHelperService> helperService;
  @Autowired private TransactionWrapper transactionWrapper;
  @Autowired private IModelSchema schema;
  @Autowired private AnonymousUserRepository anonymousUserRepository;
  @Autowired private OneTimePasswordRepository oneTimePasswordRepository;
  @Autowired private RefModelRepository refModelRepository;
  @Autowired private ThingRepository thingRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private UserSessionRepository userSessionRepository;
  @Autowired private ObjectFactory<AppSessionProvider> provider;

  @PostMapping(path = "/mutate", produces = MediaType.APPLICATION_JSON_VALUE)
  public String run(@RequestBody String query) throws Exception {
    JSONObject req = new JSONObject(query);
    List<Field> fields = parseFields(req);
    JSONObject variables = req.getJSONObject("variables");
    String queryStr = null;
    try {
      queryStr = req.getString("query");
    } catch (JSONException e) {
    }
    return executeFields(queryStr, fields, variables);
  }

  public String executeFields(String query, List<Field> fields, JSONObject variables)
      throws Exception {
    JSONObject data = new JSONObject();
    for (Field s : fields) {
      String name = s.getAlias() == null ? s.getName() : s.getAlias();
      long time = System.currentTimeMillis();
      D3ELogger.info("Started: " + time + ":" + s.getName());
      transactionWrapper.doInTransaction(
          () -> {
            try {
              try {
                Object res = executeOperation(query, s, variables);
                data.put(name, res);
              } catch (ValidationFailedException e) {
                data.put(name, createFailureResult(s, e));
              }
            } catch (Exception e2) {
              throw new RuntimeException(e2);
            }
          });
      D3ELogger.info("Completed: " + time + ":" + s.getName());
    }
    JSONObject output = new JSONObject();
    output.put("data", data);
    return output.toString();
  }

  private JSONObject createSuccessResult(Object value, Field field, String type)
      throws JSONException {
    JSONObject result = new JSONObject();
    result.put("status", MutateResultStatus.Success);
    result.put("errors", new JSONArray());
    if (value != null) {
      result.put(
          "value",
          new GraphQLDataFetcher(schema, true).fetch(inspect(field, "value"), type, value));
    }
    return result;
  }

  private JSONObject createFailureResult(Field field, ValidationFailedException e)
      throws JSONException {
    JSONObject result = new JSONObject();
    if (e.hasStatus()) {
      result.put("status", e.getStatus());
    } else {
      result.put("status", MutateResultStatus.ValidationFail);
    }
    JSONArray array = new JSONArray();
    e.getErrors().forEach(s -> array.put(s));
    result.put("errors", array);
    logErrors(result);
    return result;
  }

  private Object executeOperation(String query, Field field, JSONObject variables)
      throws Exception {
    GraphQLInputContext ctx =
        new ArgumentInputContext(
            field.getArguments(),
            helperService.getObject(),
            new HashMap<>(),
            new HashMap<>(),
            variables);
    D3ELogger.displayGraphQL(field.getName(), query, variables);
    switch (field.getName()) {
      case "createThing":
        {
          return createSuccessResult(createThing(ctx), field, "Thing");
        }
      case "updateThing":
        {
          return createSuccessResult(updateThing(ctx), field, "Thing");
        }
      case "deleteThing":
        {
          deleteThing(ctx);
          return createSuccessResult(null, field, "Thing");
        }
    }
    D3ELogger.info("Mutation Not found");
    return null;
  }

  private Thing createThing(GraphQLInputContext ctx) throws Exception {
    User currentUser = CurrentUser.get();
    if (!(currentUser instanceof AnonymousUser)) {
      throw new ValidationFailedException(
          MutateResultStatus.AuthFail,
          ListExt.asList("Current user type does not have create permissions for this model."));
    }
    Thing newThing = ctx.readChild("input", "Thing");
    this.mutator.save(newThing, false);
    return newThing;
  }

  private Thing updateThing(GraphQLInputContext ctx) throws Exception {
    User currentUser = CurrentUser.get();
    if (!(currentUser instanceof AnonymousUser)) {
      throw new ValidationFailedException(
          MutateResultStatus.AuthFail,
          ListExt.asList("Current user type does not have update permissions for this model."));
    }
    ThingEntityHelper thingHelper = this.mutator.getHelper("Thing");
    Thing currentThing = thingRepository.findById(ctx.readLong("input", "id"));
    if (currentThing == null) {
      throw new ValidationFailedException(
          MutateResultStatus.BadRequest, ListExt.asList("Invalid ID."));
    }
    currentThing.recordOld(CloneContext.forCloneable(currentThing, false));
    Thing newThing = ctx.readChild("input", "Thing");
    this.mutator.update(newThing, false);
    return newThing;
  }

  private void deleteThing(GraphQLInputContext ctx) throws Exception {
    User currentUser = CurrentUser.get();
    if (!(currentUser instanceof AnonymousUser)) {
      throw new ValidationFailedException(
          MutateResultStatus.AuthFail,
          ListExt.asList("Current user type does not have delete permissions for this model."));
    }
    long gqlInputId = ctx.readLong("input");
    ThingEntityHelper thingHelper = this.mutator.getHelper("Thing");
    Thing currentThing = thingRepository.findById(gqlInputId);
    if (currentThing == null) {
      throw new ValidationFailedException(
          MutateResultStatus.BadRequest, ListExt.asList("Invalid ID"));
    }
    this.mutator.delete(currentThing, false);
  }

  private String generateToken() {
    char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    return generateRandomString(chars, 32);
  }

  private String generateDemoCode() {
    String digits = "1234567890";
    return digits.substring(0, 4);
  }

  private String generateCode() {
    char[] digits = "1234567890".toCharArray();
    return generateRandomString(digits, 4);
  }

  private String generateRandomString(char[] array, int length) {
    StringBuilder sb = new StringBuilder(length);
    Random rnd = new Random();
    for (int i = 0; i < length; i++) {
      char c = array[rnd.nextInt(array.length)];
      sb.append(c);
    }
    return sb.toString();
  }
}

package rest;

import d3e.core.SchemaConstants;
import gqltosql.schema.DModel;
import gqltosql.schema.FieldPrimitiveType;
import java.util.HashMap;
import java.util.Map;
import models.AnonymousUser;
import models.Avatar;
import models.ChangePasswordRequest;
import models.ChildModel;
import models.D3EImage;
import models.D3EMessage;
import models.EmailMessage;
import models.OneTimePassword;
import models.PushNotification;
import models.RefModel;
import models.ReportConfig;
import models.ReportConfigOption;
import models.SMSMessage;
import models.Thing;
import models.User;
import models.UserSession;

public class ModelSchema1 {
  private Map<String, DModel<?>> allTypes = new HashMap<>();

  public ModelSchema1(Map<String, DModel<?>> allTypes) {
    this.allTypes = allTypes;
  }

  public void createAllTables() {
    addAnonymousUserFields();
    addAvatarFields();
    addChangePasswordRequestFields();
    addChildModelFields();
    addD3EImageFields();
    addD3EMessageFields();
    addEmailMessageFields();
    addOneTimePasswordFields();
    addPushNotificationFields();
    addRefModelFields();
    addReportConfigFields();
    addReportConfigOptionFields();
    addSMSMessageFields();
    addThingFields();
    addUserFields();
    addUserSessionFields();
  }

  public DModel<?> getType(String type) {
    return allTypes.get(type);
  }

  public <T> DModel<T> getType2(String type) {
    return ((DModel<T>) allTypes.get(type));
  }

  private void addAnonymousUserFields() {
    DModel<AnonymousUser> m = getType2("AnonymousUser");
    m.setParent(getType("User"));
  }

  private void addAvatarFields() {
    DModel<Avatar> m = getType2("Avatar");
    m.addEmbedded(
        "image",
        Avatar._IMAGE,
        "_image_id",
        "_image",
        getType("D3EImage"),
        (s) -> s.getImage(),
        (s, v) -> s.setImage(v));
    m.addPrimitive(
        "createFrom",
        Avatar._CREATEFROM,
        "_create_from",
        FieldPrimitiveType.String,
        (s) -> s.getCreateFrom(),
        (s, v) -> s.setCreateFrom(v));
  }

  private void addChangePasswordRequestFields() {
    DModel<ChangePasswordRequest> m = getType2("ChangePasswordRequest");
    m.addPrimitive(
        "newPassword",
        ChangePasswordRequest._NEWPASSWORD,
        "_new_password",
        FieldPrimitiveType.String,
        (s) -> s.getNewPassword(),
        (s, v) -> s.setNewPassword(v));
  }

  private void addChildModelFields() {
    DModel<ChildModel> m = getType2("ChildModel");
    m.addPrimitive(
        "num",
        ChildModel._NUM,
        "_num",
        FieldPrimitiveType.Integer,
        (s) -> s.getNum(),
        (s, v) -> s.setNum(v));
  }

  private void addD3EImageFields() {
    DModel<D3EImage> m = getType2("D3EImage");
    m.addPrimitive(
        "size",
        D3EImage._SIZE,
        "_size",
        FieldPrimitiveType.Integer,
        (s) -> s.getSize(),
        (s, v) -> s.setSize(v));
    m.addPrimitive(
        "width",
        D3EImage._WIDTH,
        "_width",
        FieldPrimitiveType.Integer,
        (s) -> s.getWidth(),
        (s, v) -> s.setWidth(v));
    m.addPrimitive(
        "height",
        D3EImage._HEIGHT,
        "_height",
        FieldPrimitiveType.Integer,
        (s) -> s.getHeight(),
        (s, v) -> s.setHeight(v));
    m.addReference(
        "file",
        D3EImage._FILE,
        "_file_id",
        false,
        getType("DFile"),
        (s) -> s.getFile(),
        (s, v) -> s.setFile(v));
  }

  private void addD3EMessageFields() {
    DModel<D3EMessage> m = getType2("D3EMessage");
    m.addPrimitive(
        "from",
        D3EMessage._FROM,
        "_from",
        FieldPrimitiveType.String,
        (s) -> s.getFrom(),
        (s, v) -> s.setFrom(v));
    m.addPrimitiveCollection(
        "to",
        D3EMessage._TO,
        "_to",
        "_d3emessage_to_aead9a",
        FieldPrimitiveType.String,
        (s) -> s.getTo(),
        (s, v) -> s.setTo(v));
    m.addPrimitive(
        "body",
        D3EMessage._BODY,
        "_body",
        FieldPrimitiveType.String,
        (s) -> s.getBody(),
        (s, v) -> s.setBody(v));
    m.addPrimitive(
        "createdOn",
        D3EMessage._CREATEDON,
        "_created_on",
        FieldPrimitiveType.DateTime,
        (s) -> s.getCreatedOn(),
        (s, v) -> s.setCreatedOn(v));
  }

  private void addEmailMessageFields() {
    DModel<EmailMessage> m = getType2("EmailMessage");
    m.setParent(getType("D3EMessage"));
    m.addPrimitiveCollection(
        "bcc",
        EmailMessage._BCC,
        "_bcc",
        "_email_message_bcc_1e1d14",
        FieldPrimitiveType.String,
        (s) -> s.getBcc(),
        (s, v) -> s.setBcc(v));
    m.addPrimitiveCollection(
        "cc",
        EmailMessage._CC,
        "_cc",
        "_email_message_cc_ad6675",
        FieldPrimitiveType.String,
        (s) -> s.getCc(),
        (s, v) -> s.setCc(v));
    m.addPrimitive(
        "subject",
        EmailMessage._SUBJECT,
        "_subject",
        FieldPrimitiveType.String,
        (s) -> s.getSubject(),
        (s, v) -> s.setSubject(v));
    m.addPrimitive(
        "html",
        EmailMessage._HTML,
        "_html",
        FieldPrimitiveType.Boolean,
        (s) -> s.isHtml(),
        (s, v) -> s.setHtml(v));
    m.addReferenceCollection(
        "inlineAttachments",
        EmailMessage._INLINEATTACHMENTS,
        "_inline_attachments_id",
        "_email_message_inline_attachments_6efef9",
        false,
        getType("DFile"),
        (s) -> s.getInlineAttachments(),
        (s, v) -> s.setInlineAttachments(v));
    m.addReferenceCollection(
        "attachments",
        EmailMessage._ATTACHMENTS,
        "_attachments_id",
        "_email_message_attachments_ead2e3",
        false,
        getType("DFile"),
        (s) -> s.getAttachments(),
        (s, v) -> s.setAttachments(v));
  }

  private void addOneTimePasswordFields() {
    DModel<OneTimePassword> m = getType2("OneTimePassword");
    m.addPrimitive(
        "input",
        OneTimePassword._INPUT,
        "_input",
        FieldPrimitiveType.String,
        (s) -> s.getInput(),
        (s, v) -> s.setInput(v));
    m.addPrimitive(
        "inputType",
        OneTimePassword._INPUTTYPE,
        "_input_type",
        FieldPrimitiveType.String,
        (s) -> s.getInputType(),
        (s, v) -> s.setInputType(v));
    m.addPrimitive(
        "userType",
        OneTimePassword._USERTYPE,
        "_user_type",
        FieldPrimitiveType.String,
        (s) -> s.getUserType(),
        (s, v) -> s.setUserType(v));
    m.addPrimitive(
        "success",
        OneTimePassword._SUCCESS,
        "_success",
        FieldPrimitiveType.Boolean,
        (s) -> s.isSuccess(),
        (s, v) -> s.setSuccess(v));
    m.addPrimitive(
        "errorMsg",
        OneTimePassword._ERRORMSG,
        "_error_msg",
        FieldPrimitiveType.String,
        (s) -> s.getErrorMsg(),
        (s, v) -> s.setErrorMsg(v));
    m.addPrimitive(
        "token",
        OneTimePassword._TOKEN,
        "_token",
        FieldPrimitiveType.String,
        (s) -> s.getToken(),
        (s, v) -> s.setToken(v));
    m.addPrimitive(
        "code",
        OneTimePassword._CODE,
        "_code",
        FieldPrimitiveType.String,
        (s) -> s.getCode(),
        (s, v) -> s.setCode(v));
    m.addReference(
        "user",
        OneTimePassword._USER,
        "_user_id",
        false,
        getType("User"),
        (s) -> s.getUser(),
        (s, v) -> s.setUser(v));
    m.addPrimitive(
        "expiry",
        OneTimePassword._EXPIRY,
        "_expiry",
        FieldPrimitiveType.DateTime,
        (s) -> s.getExpiry(),
        (s, v) -> s.setExpiry(v));
  }

  private void addPushNotificationFields() {
    DModel<PushNotification> m = getType2("PushNotification");
    m.addPrimitiveCollection(
        "deviceTokens",
        PushNotification._DEVICETOKENS,
        "_device_tokens",
        "_push_notification_device_tokens_059c0d",
        FieldPrimitiveType.String,
        (s) -> s.getDeviceTokens(),
        (s, v) -> s.setDeviceTokens(v));
    m.addPrimitive(
        "title",
        PushNotification._TITLE,
        "_title",
        FieldPrimitiveType.String,
        (s) -> s.getTitle(),
        (s, v) -> s.setTitle(v));
    m.addPrimitive(
        "body",
        PushNotification._BODY,
        "_body",
        FieldPrimitiveType.String,
        (s) -> s.getBody(),
        (s, v) -> s.setBody(v));
    m.addPrimitive(
        "path",
        PushNotification._PATH,
        "_path",
        FieldPrimitiveType.String,
        (s) -> s.getPath(),
        (s, v) -> s.setPath(v));
  }

  private void addRefModelFields() {
    DModel<RefModel> m = getType2("RefModel");
    m.addPrimitive(
        "num",
        RefModel._NUM,
        "_num",
        FieldPrimitiveType.Integer,
        (s) -> s.getNum(),
        (s, v) -> s.setNum(v));
  }

  private void addReportConfigFields() {
    DModel<ReportConfig> m = getType2("ReportConfig");
    m.addPrimitive(
        "identity",
        ReportConfig._IDENTITY,
        "_identity",
        FieldPrimitiveType.String,
        (s) -> s.getIdentity(),
        (s, v) -> s.setIdentity(v));
    m.addReferenceCollection(
        "values",
        ReportConfig._VALUES,
        "_values_id",
        "_report_config_values_a912b7",
        true,
        getType("ReportConfigOption"),
        (s) -> s.getValues(),
        (s, v) -> s.setValues(v));
  }

  private void addReportConfigOptionFields() {
    DModel<ReportConfigOption> m = getType2("ReportConfigOption");
    m.addPrimitive(
        "identity",
        ReportConfigOption._IDENTITY,
        "_identity",
        FieldPrimitiveType.String,
        (s) -> s.getIdentity(),
        (s, v) -> s.setIdentity(v));
    m.addPrimitive(
        "value",
        ReportConfigOption._VALUE,
        "_value",
        FieldPrimitiveType.String,
        (s) -> s.getValue(),
        (s, v) -> s.setValue(v));
  }

  private void addSMSMessageFields() {
    DModel<SMSMessage> m = getType2("SMSMessage");
    m.setParent(getType("D3EMessage"));
    m.addPrimitive(
        "dltTemplateId",
        SMSMessage._DLTTEMPLATEID,
        "_dlt_template_id",
        FieldPrimitiveType.String,
        (s) -> s.getDltTemplateId(),
        (s, v) -> s.setDltTemplateId(v));
  }

  private void addThingFields() {
    DModel<Thing> m = getType2("Thing");
    m.addPrimitive(
        "msg",
        Thing._MSG,
        "_msg",
        FieldPrimitiveType.String,
        (s) -> s.getMsg(),
        (s, v) -> s.setMsg(v));
    m.addReference(
        "child",
        Thing._CHILD,
        "_child_id",
        true,
        getType("ChildModel"),
        (s) -> s.getChild(),
        (s, v) -> s.setChild(v));
    m.addReferenceCollection(
        "childColl",
        Thing._CHILDCOLL,
        "_child_coll_id",
        "_thing_child_coll_8127ab",
        true,
        getType("ChildModel"),
        (s) -> s.getChildColl(),
        (s, v) -> s.setChildColl(v));
  }

  private void addUserFields() {
    DModel<User> m = getType2("User");
    m.addPrimitive(
        "isActive",
        User._ISACTIVE,
        "_is_active",
        FieldPrimitiveType.Boolean,
        (s) -> s.isIsActive(),
        (s, v) -> s.setIsActive(v));
    m.addPrimitive(
        "deviceToken",
        User._DEVICETOKEN,
        "_device_token",
        FieldPrimitiveType.String,
        (s) -> s.getDeviceToken(),
        (s, v) -> s.setDeviceToken(v));
  }

  private void addUserSessionFields() {
    DModel<UserSession> m = getType2("UserSession");
    m.addPrimitive(
        "userSessionId",
        UserSession._USERSESSIONID,
        "_user_session_id",
        FieldPrimitiveType.String,
        (s) -> s.getUserSessionId(),
        (s, v) -> s.setUserSessionId(v));
  }
}

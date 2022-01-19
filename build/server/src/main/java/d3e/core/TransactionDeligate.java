package d3e.core;

import java.io.IOException;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import store.EntityMutator;

@Component
public class TransactionDeligate {

	public static interface ToRun {
		void run() throws ServletException, IOException;
	}

	@Autowired
	private EntityMutator entityMutator;

	@Transactional
	public void run(boolean finish, ToRun run) throws ServletException, IOException {
		run.run();
		if (finish) {
			entityMutator.finish();
		}
	}

	@Transactional(readOnly = true)
	public void readOnly(ToRun run) throws ServletException, IOException {
		run.run();
	}
}

package d3e.core;

import org.springframework.transaction.TransactionException;

public class GraphQLFilter extends org.springframework.web.filter.OncePerRequestFilter {

	private TransactionWrapper wrapper;

	public GraphQLFilter(TransactionWrapper wrapper) {
		this.wrapper = wrapper;
	}

	@java.lang.Override
	protected void doFilterInternal(javax.servlet.http.HttpServletRequest request,
			javax.servlet.http.HttpServletResponse response, javax.servlet.FilterChain filterChain)
			throws javax.servlet.ServletException, java.io.IOException {
		try {
			wrapper.doInTransaction(() -> filterChain.doFilter(request, response));
		} catch (TransactionException e) {
		}
	}

	@Override
	protected boolean shouldNotFilterAsyncDispatch() {
		return false;
	}
}

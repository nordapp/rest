package org.i3xx.util.client.wk;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Process {

	final Action action;
	final Input input;
	final Result result;
	final Exception exception;
	final Map<String, Object> attributes;
	
	/**
	 * @param action
	 * @param input
	 * @param result
	 * @param exception
	 * @param attributes
	 */
	private Process(Action action, Input input, Result result, Exception exception, Map<String, Object> attributes) {
		super();
		this.action = action;
		this.input = input;
		this.result = result;
		this.exception = exception;
		this.attributes = attributes;
	}
	
	/**
	 * Creates a new connection and sets the action
	 * 
	 * @param action The action
	 * @return
	 */
	public static Process create(Action action) {
		return new Process(action, null, null, null, new LinkedHashMap<String, Object>());
	}
	
	/**
	 * @param action The next action
	 * @return
	 */
	public Process cont(Action action) {
		return new Process(action, input, result, exception, attributes);
	}
	
	/**
	 * Sets the (initial) input
	 * 
	 * @return
	 */
	public Process input(InputFx func) {
		return new Process(action, new InputImpl(func), result, exception, attributes);
	}
	
	/**
	 * Sets the (initial) result
	 * 
	 * @return
	 */
	public Process result(ResultFx func) {
		return new Process(action, input, new ResultImpl(func), exception, attributes);
	}
	
	/**
	 * @return
	 */
	public Process copy() {
		return new Process(action, input, result, exception, attributes);
	}
	
	/**
	 * Moves the result to the input and sets the result to null.
	 * 
	 * @return
	 */
	public Process shift() {
		return new Process(action, new InputImpl(result.getResult()), null, exception, attributes);
	}
	
	/**
	 * @return
	 */
	public Process reset() {
		return new Process(action, null, null, null, new LinkedHashMap<String, Object>());
	}
	
	/**
	 * Gets the attributes
	 * 
	 * @return
	 */
	public Map<String, Object> a() {
		return attributes;
	}
	
	/**
	 * Gets the input
	 * 
	 * @return
	 */
	public Input i() {
		return input;
	}
	
	/**
	 * Gets the result
	 * 
	 * @return
	 */
	public Result r() {
		return result;
	}
	
	/**
	 * Gets the exception
	 * 
	 * @return
	 */
	public Exception e() {
		return exception;
	}
	
	/**
	 * Gets the attributes
	 * 
	 * @return
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	
	/**
	 * @return
	 */
	public Input getInput() {
		return input;
	}
	
	/**
	 * @return
	 */
	public Result getResult() {
		return result;
	}
	
	/**
	 * @return
	 */
	public Exception getException() {
		return exception;
	}
	
	/**
	 * @param code
	 */
	public void setReturnCode(int code) {
		attributes.put("__return_code__", new Integer(code));
	}
	
	/**
	 * @return
	 */
	public int getReturnCode() {
		return attributes.containsKey("__return_code__") ?((Integer)attributes.get("__return_code__")).intValue() : 0;
	}
	
	/**
	 * Executes the action
	 * 
	 * @return
	 */
	public Process exec() {
		try{
			Result r = new ResultImpl( action.run(this) );
			return new Process(action, input, r, exception, attributes);
		}catch(Exception e){
			return new Process(action, input, result, e, attributes);
		}
	}
	
	/**
	 * Executes the action
	 * 
	 * @param filter
	 * @return
	 */
	public Process exec(Filter filter) {
		if(filter.match(this)) {
			return exec();
		}else{
			return this;
		}
	}
	
	/**
	 * Executes the action and moves the result to the input, the result is set to null.
	 * 
	 * @return
	 */
	public Process execAndShift() {
		try{
			Input i = new InputImpl( action.run(this) );
			return new Process(action, i, null, exception, attributes);
		}catch(Exception e){
			return new Process(action, input, result, e, attributes);
		}
	}
	
	/**
	 * Executes the action and moves the result to the input, the result is set to null.
	 * 
	 * @param filter
	 * @return
	 */
	public Process execAndShift(Filter filter) {
		if(filter.match(this)) {
			return execAndShift();
		}else{
			return this;
		}
	}
	
	/**
	 * Executes the action
	 * 
	 * A NullPointerException occurs if there is an access to the result
	 * for the first time without setting the initial result. The filter
	 * uses c.r() or c.getResult() and for the first time the result is
	 * set to null. To set an initial result use result(()-><value>).
	 * 
	 * @param filter
	 * @return
	 */
	public Process loop(Filter filter) {
		Process op = this;
		while(filter.match(op)) {
			try {
				Result r = new ResultImpl( action.run(op) );
				op = new Process(action, input, r, exception, attributes);
			} catch (IOException e) {
				op = new Process(action, input, result, e, attributes);
			}
		}//while
		
		return op;
	}
}

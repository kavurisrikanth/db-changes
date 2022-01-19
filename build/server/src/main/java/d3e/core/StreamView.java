package d3e.core;
import java.util.stream.Stream;
public class StreamView<T> {
	  StreamView(Stream<T> stream){
		  
	  }

	   public static boolean getIsBroadcast() {
	    	//TODO
		   return true;
	    }

	  /*  public Stream<T> asBroadcastStream({void onListen(StreamSubscription<T> subscription), void onCancel(StreamSubscription<T> subscription)}){
	    //TODO
	    	return null;
	    }

	    public StreamSubscription<T> listen(void onData(T value), {Function onError, void onDone(), boolean cancelOnError}){
	    	//TODO
	    	return null;
	    }*/
}

import java.util.*;

class UnambiguousResolutionException extends Exception
{
	ArrayList<Method> met;
	UnambiguousResolutionException(ArrayList<Method> met2)
	{
		this.met =met2;
	}
}
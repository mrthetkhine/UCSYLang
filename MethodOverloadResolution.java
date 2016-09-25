import java.util.*;

class MethodOverloadResolution
{
	static int getNoOfParameter(String methodSignature)
	{
		String parameters[] ;
		
		String par = methodSignature.substring(1,methodSignature.length()-1);
		
		if(par.equals(""))
		{
			return 0;
		}
		else
		{
			parameters = par.split(",");	
			return parameters.length;
		}
		
		
	}
	static String[] splitParameter(String methodSignature)
	{
		String parameters[] ;
		
		String par = methodSignature.substring(1,methodSignature.length()-1);
		
		if(par.equals(""))
		{
			return Method.emptyParameter;
		}
		else
		{
			parameters = par.split(",");	
			return parameters;
		}
	}
	static int findExactMatch(ArrayList<Method> allMethodToOverload, String methodSignature)
	{
		for (int i = 0; i< allMethodToOverload.size(); i++)
		{
			if( allMethodToOverload.get(i).methodSignature.equals(methodSignature))
			{
				return i;
			}
		}
		return -1;
	}
	static boolean canAllParameterCanPromoteUsingRuleOne(Method m, String methodSignature)
	{
		String methodParameters[] = splitParameter(methodSignature);
		
		for (int i = 0; i< methodParameters.length; i++)
		{
			//System.out.println ("Compare par "+ m.parameters[i]+ " "+ methodParameters[i]);
			if(! CentralTypeTable.canPromoteUsingRuleOne(  methodParameters[i] ,m.parameters[i]) )
			{
				return false;
			}
		}
		return true;
	}
	static boolean canAllParameterCanPromoteUsingRuleTwo(Method m, String methodSignature)
	{
		String methodParameters[] = splitParameter(methodSignature);
		
		for (int i = 0; i< methodParameters.length; i++)
		{
			//System.out.println ("Compare par "+ m.parameters[i]+ " "+ methodParameters[i]);
			try
			{
				Type t = CentralTypeTable.getCentralTypeTable().getType(methodParameters[i]);
				if(!t.assignmentCompatiable(m.parameters[i]))
				{
					return false;
				}
			}
			catch(Exception e)
			{
				return false;
			}
		}
		return true;
	}
	static ArrayList<Method> findBestOverloadUsingRuleOne(ArrayList<Method> allMethod, String methodSignature)
	{
		ArrayList<Method> ruleOneMethod = new ArrayList<Method>();
		
		for (int i = 0; i< allMethod.size(); i++)
		{
			Method met = allMethod.get(i);
			if( canAllParameterCanPromoteUsingRuleOne( met, methodSignature))
			{
				ruleOneMethod.add(met);
			}
		}
		return ruleOneMethod;
	}
	static ArrayList<Method> findBestOverloadUsingRuleTwo(ArrayList<Method> allMethod, String methodSignature)
	{
		ArrayList<Method> ruleTwoMethod = new ArrayList<Method>();
		
		for (int i = 0; i< allMethod.size(); i++)
		{
			Method met = allMethod.get(i);
			if( canAllParameterCanPromoteUsingRuleTwo( met, methodSignature))
			{
				ruleTwoMethod.add(met);
			}
		}
		return ruleTwoMethod;
	}	
	static Method findBestOverloadedMethod(ArrayList<Method> allMethodToOverload, String methodSignature)
	throws UnambiguousResolutionException , CannotFindBestOverloadedMethodException
	{
		Method bestMethod = null;
		int exactMatch = MethodOverloadResolution.findExactMatch(allMethodToOverload,methodSignature);
			
			if( exactMatch != -1)
			{
				bestMethod = allMethodToOverload.get(exactMatch);
				return bestMethod;
				//System.out.println ("Found eact match "+ allMethodToOverload.get(exactMatch).methodName +" "+ allMethodToOverload.get(exactMatch).methodSignature);
			}
			else
			{
				ArrayList<Method> ruleOneMethod = MethodOverloadResolution.findBestOverloadUsingRuleOne(allMethodToOverload,methodSignature);
				
				if(ruleOneMethod.size()== 0 )
				{
					//Find using rule Two
					ArrayList<Method> ruleTwoMethod = MethodOverloadResolution.findBestOverloadUsingRuleTwo(allMethodToOverload,methodSignature);;
					if( ruleTwoMethod.size() > 1 )
					{
						throw new UnambiguousResolutionException(ruleTwoMethod);
					}
					else if(ruleTwoMethod.size() == 1)
					{
						bestMethod = ruleTwoMethod.get(0);
						return bestMethod;
					}
					else
					{
						throw new CannotFindBestOverloadedMethodException();
					}
				}
				else if( ruleOneMethod.size() > 1 )
				{
					throw new UnambiguousResolutionException(ruleOneMethod);
				}
				else
				{
					bestMethod = ruleOneMethod.get(0);
					return bestMethod;
				}
				
			}
		
	}
}
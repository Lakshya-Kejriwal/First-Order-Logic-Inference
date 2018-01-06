package homework;


import java.util.*;
import java.io.*;

class Tree
{
  public Literal Literal = new Literal();
  public Tree child_1;
  public Tree child_2;
  public Tree(Literal a)
  {
	Literal = a;
	child_1 = null;
	child_2 = null;
  }
}

class Literal
{
	public String name;
	public Vector<String> variable = new Vector<String>();
	public int category;
	public boolean negation;
}

class expr
{
  public int category;
  public expr(int category)
  {
	this.category = category;
  }
}

class variable_arr extends expr
{
  public Vector<String> value = new Vector<String>();
  public variable_arr(Vector<String> value)
  {
	  super(3);
	this.value = value;
  }
}

class var extends expr
{
  public String value;
  public var(String value)
  {
	  super(2);
	this.value = value;
  }
}

class element extends expr
{
  public Literal value = new Literal();
  public element(Literal value)
  {
	super(4);
	this.value = value;
  }
}


class constant extends expr
{
  public String value;
  public constant(String value)
  {
	  super(1);
	this.value = value;
  }
}

class variable_unify
{
  public Map<String,String> variable_val = new HashMap<String,String>(); 

  public boolean result_unify;
  
  public variable_unify(boolean initial)
  {
	  this.result_unify = initial;
  }

}

class Compare_knowledge_base implements Comparator<Vector<Literal>>{

	public int compare(Vector<Literal> c1, Vector<Literal> c2)
	{
	  return c1.size() - c2.size();
	}
}

class Compare_literals implements Comparator<Literal>
{
public int compare(Literal a, Literal b)
{
  int value = a.name.compareTo(b.name);

  int x = 0;
  if (value == 0)
  {
	  value = a.variable.get(0).compareTo(b.variable.get(0));
	  return value;
  }
  return value;
}
}

final class StringFunctions
{
	private static String activeString;
	private static int activePosition;
	public static String strTok(String stringToTokenize, String delimiters)
	{
		if (stringToTokenize != null)
		{
			activeString = stringToTokenize;
			activePosition = -1;
		}

		//the stringToTokenize was never set:
		if (activeString == null)
		{
			return null;
		}

		//all tokens have already been extracted:
		if (activePosition == activeString.length())
		{
			return null;
		}

		//bypass delimiters:
		activePosition++;
		while (activePosition < activeString.length() && delimiters.indexOf(activeString.charAt(activePosition)) > -1)
		{
			activePosition++;
		}

		//only delimiters were left, so return null:
		if (activePosition == activeString.length())
		{
			return null;
		}

		//get starting position of string to return:
		int startingPosition = activePosition;

		//read until next delimiter:
		do
		{
			activePosition++;
		} while (activePosition < activeString.length() && delimiters.indexOf(activeString.charAt(activePosition)) == -1);

		return activeString.substring(startingPosition, activePosition);
	}
}


public class homework
{
	
	public static Literal copyLiteral(Literal a)
	{
	  Literal new_lit = new Literal();
	  new_lit.name = a.name;
	  new_lit.negation = a.negation;
	  new_lit.variable.addAll(a.variable);
	  return new_lit;
	}

	public static Vector<Literal> queries = new Vector<Literal>();

	public static Vector<Vector<Literal>> knowledge_base = new Vector<Vector<Literal>>();

	public static Map<String,Integer> variables = new HashMap<String,Integer>();

	public static void fromTreeToClause(Tree root, Vector<Literal> clause)
	{
	  if (root == null)
	  {
		  return;
	  }

	  fromTreeToClause(root.child_1, clause);
	  if (root.Literal.category == 5)
	  {
		clause.add(copyLiteral((root.Literal)));
	  }
	  fromTreeToClause(root.child_2, clause);

	}

	public static Vector<String> split(String str,String sep)
	{
		String cstr = str;
		String current;
		Vector<String> array = new Vector<String>();
		current = StringFunctions.strTok(cstr,sep);
		while (current != null)
		{
			array.add(current);
			current = StringFunctions.strTok(null,sep);
		}
		return array;
	}

	public static Literal getLiterals(String sentence)
	{
	  Vector<String> sen1 = new Vector<String>();
	  Vector<String> sen2 = new Vector<String>();

	  Literal Literal = new Literal();
	  
	  switch(sentence.charAt(0))
	  {
	  case '~':
		  Literal.negation = true;
		  sentence = sentence.substring(1);
		  break;
	default:
		  Literal.negation = false;
		  break;
	  }
	  
	  sen1 = split(sentence, "(");
	  Literal.name = sen1.get(0);
	  sen2 = split(sen1.get(1), ",");
	  for (String s1 : sen2)
	  {
		  Literal.variable.add(s1);
	  }

	  Literal.variable.setElementAt(Literal.variable.get(Literal.variable.size() - 1).substring(0,Literal.variable.get(Literal.variable.size() - 1).length() - 1), Literal.variable.size() - 1);
	  
	  sen1.clear();
	  sen2.clear();
	  return Literal;
	}


	public static int precedence(char c)
	{
		if(c == '&' || c == '|')
		{
			return 2;
		}
		else if(c == '=')
		{
			return 1;
		}
		else if(c == '~')
		{
			return 3;
		}
		else
		{
			return -1;
		}
	}

	public static void standardise(Vector<Literal> clause)
	{

	Map<String,String> local_var = new HashMap<String,String>();
	for (Literal a : clause)
	{
		int x = 0;
	  while(x < a.variable.size())
	  {
		if (!Character.isUpperCase(a.variable.get(x).charAt(0)))
		{
		if (local_var.containsKey(a.variable.get(x)) == false)
		{
		  int counter = 0;
		  String old_variable = a.variable.get(x);
		  if (variables.containsKey(a.variable.get(x)) != false)
		  {
			  counter = variables.get(a.variable.get(x)) + 1;
			  a.variable.set(x, a.variable.get(x) + Integer.toString(counter));
		  }
		  variables.put(old_variable,counter);
		  local_var.put(old_variable,a.variable.get(x));

		}
		else
		{
		  a.variable.set(x, local_var.get(a.variable.get(x)));
		}
		}
		x++;
	  }
	}

	}
	

	public static variable_unify doUnification(expr temp1, expr temp2, variable_unify mapping)
	{

	if (mapping.result_unify)
	{
		return mapping;
	}
	
	int i = temp1.category;
	int j = temp2.category;
	
//    element c_x = (element)temp1;
//    element c_y = (element)temp2;

	if (i == 1 && j == 1)
	{
		constant cx = (constant)temp1;
		constant cy = (constant)temp2;
		String val1 = cx.value;
		String val2 = cy.value;
		if (val1.compareTo(val2) == 0)
		{
		  return mapping;
		}
		else
		{
			mapping.result_unify = true;
		}
		return mapping;
	}
	else if (i == 2)
	{
		return variableUnify((var)temp1, temp2, mapping);
	}
	else if (j == 2)
	{
		return variableUnify((var)temp2, temp1, mapping);
	}
	else if (i == 3 && j == 3)
	{

	  variable_arr first = (variable_arr)temp1;
	  variable_arr second = (variable_arr)temp2;

	  expr x = null;
	  expr y = null;
	  
	  try
	  {
		  x = Character.isUpperCase(first.value.get(0).charAt(0)) ? new constant(first.value.get(0)) : new var(first.value.get(0));
		  y = Character.isUpperCase(second.value.get(0).charAt(0)) ? new constant(second.value.get(0)) : new var(second.value.get(0));
	  }
	  catch(Exception e)
	  {
		  if (first.value.size() == 0 || second.value.size() == 0)
		  {
			  return mapping;
		  }
	  }

	  first.value.remove(0);

	  second.value.remove(0);

	  return doUnification(temp1, temp2, doUnification(x,y,mapping));

	}
	else if (i == 4 && j == 4)
	{
		
	    element c_x = (element)temp1;
	    element c_y = (element)temp2;

	  @SuppressWarnings("unchecked")
	Vector<String> var_data1 = (Vector<String>)c_x.value.variable.clone();
	  variable_arr first = new variable_arr(var_data1);

	  @SuppressWarnings("unchecked")
	Vector<String> var_data2 = (Vector<String>)c_y.value.variable.clone();
	  variable_arr second = new variable_arr(var_data2);
	  
	  constant constant_x = new constant(c_x.value.name);
	  constant constant_y = new constant(c_y.value.name);

	  return doUnification(first, second, doUnification(constant_x,constant_y,mapping));

	}
	else
	{
	  mapping.result_unify = true;
	  return mapping;
	}

	}
	
	public static String against(variable_unify mapping, String key)
	{
		String main_val = mapping.variable_val.get(key);
		for(String key_1:mapping.variable_val.keySet())
		{
			if(mapping.variable_val.get(key_1).equals(key))
			{
					continue;
			}
			String val = mapping.variable_val.get(key_1);
			if(!main_val.equals(key_1))
			{
				continue;
			}
			if(Character.isUpperCase(val.charAt(0)))
			{
				return val;
			}
		}
		
		return mapping.variable_val.get(key);
	}
	
	public static boolean compareLiterals(Literal literal1, Literal literal2)
	{
		if ((literal1.negation == !literal2.negation) && (literal1.name.equals(literal2.name)))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static Vector<Vector<Literal>> resolution(Vector<Literal> clause1, Vector<Literal> clause2)
	{
	Vector<Vector<Literal>> new_clause = new Vector<Vector<Literal>>();
	//boolean flag = true;

	for (int i = 0; i < clause1.size() ; i++)
	{
		for (int j = 0 ; j < clause2.size() ; j++)
		{
		  Literal literal1 = clause1.get(i);
		  Literal literal2 = clause2.get(j);

		  if (compareLiterals(literal1, literal2))
		  {

			variable_unify mapping = new variable_unify(false);

			element c1 = new element(literal1);
			element c2 = new element(literal2);

			mapping = doUnification(c1, c2, mapping);
			
			for(String key_1:mapping.variable_val.keySet())
			{
				String val = against(mapping, key_1);
				mapping.variable_val.put(key_1, val);
			}
			
//			for(String key: mapping.variable_val.keySet())
//			{
//				if(Character.isLowerCase(key.charAt(0)) && Character.isLowerCase(mapping.variable_val.get(key).charAt(0)))
//						{
//							mapping.result_unify = true;
//						}
//			}
			
			if (!mapping.result_unify)
			{
				Vector<Literal> solvent = new Vector<Literal>();
	
				for (int k = 0 ; k <  clause1.size() ; k++)
				{
				  if (i != k)
				  {
					  Literal c = copyLiteral(clause1.get(k));
	
					  for (int x = 0; x < c.variable.size() ; x++)
					  {
						  String current = c.variable.get(x);
						  if (mapping.variable_val.containsKey(current))
						  {
							  c.variable.set(x, mapping.variable_val.get(current));
						  }
					  }
					  solvent.add(c);
				  }
				}
	
				for (int k = 0 ; k <  clause2.size() ; k++)
				{
				  if (j != k)
				  {
					  Literal d = copyLiteral(clause2.get(k));
	
					  for (int x = 0; x < d.variable.size() ; x++)
					  {
						  String current = d.variable.get(x);
						if (mapping.variable_val.containsKey(current))
						{
						  d.variable.set(x, mapping.variable_val.get(current));
						}
					  }
					  solvent.add(d);
				  }
				}

				new_clause.add(solvent);


			}

		  }

		}

	}
	return new_clause;
	}

	public static boolean isEmpty(Vector<Vector<Literal>> resolvents)
	{
	  for (Vector<Literal> clause : resolvents)
	  {
	  if (clause.size() == 0)
	  {
		return true;
	  }
	  }
	  return false;
	}

	public static boolean equals(Vector<Literal> clause1, Vector<Literal> clause2)
	{
	  if (clause1.size() != clause2.size())
	  {
		return false;
	  }
	  
	  Collections.sort(clause1, new Compare_literals());
	  Collections.sort(clause2, new Compare_literals());
	  
	  try
	  {
		  for (int i = 0;i < clause1.size();i++)
		  {
				if (clause1.elementAt(i).negation == clause2.elementAt(i).negation && clause1.elementAt(i).name.compareTo(clause2.elementAt(i).name) == 0)
				{
				  for (int j = 0;j < clause1.elementAt(i).variable.size();j++)
				  {
					  if (clause1.elementAt(i).variable.size() != clause2.elementAt(i).variable.size())
						  return false;
					  if (clause1.elementAt(i).variable.elementAt(j).compareTo(clause2.elementAt(i).variable.elementAt(j)) != 0)
					  {
						return false;
					  }
				  }
				}
			else
			{
				return false;
			}
		  }
	  }
	  catch(Exception e)
	  {
		  System.out.println("Something wrong");
	  }
		  
	  return true;

	}
	public static Vector<Vector<Literal>> union(Vector<Vector<Literal>> clauses1, Vector<Vector<Literal>> clauses2)
	{

	  Vector<Vector<Literal>> result = new Vector<Vector<Literal>>();

	  for (Vector<Literal> c : clauses1)
	  {
		result.add(c);

		for (int i = 0; i < clauses2.size() ;)
		{
		  if (equals(c, clauses2.get(i)))
		  {
			clauses2.remove(i);
		  }
		  else
		  {
			  i++;
		  }
		}
	  }

	  for (	Vector<Literal> c : clauses2)
	  {
	  result.add(c);
	  }
	  clauses1.clear();
	  clauses2.clear();
	  return result;
	}
	
	public static boolean checkNewClauses(Vector<Vector<Literal>> new_clauses, Vector<Vector<Literal>> old_clauses)
	{
	boolean flag = false;
	for (Vector<Literal> clause1 : new_clauses)
	{
	  flag = false;
	  for (Vector<Literal> clause2 : old_clauses)
	  {
		if (equals(clause1, clause2))
		{
			flag = true;
			break;
		}
	  }
	  if (!flag)
	  {
		  return false;
	  }

	}
	return true;
	}

	
	
	public static boolean isTrue(Vector<Literal> query)
	{

	  boolean is_query_used = false;

	  for (Literal c1 : query)
	  {
		c1.negation = !c1.negation;
	  }
	  knowledge_base.add(query);
	  Vector<Vector<Literal>> resolved_clause = new Vector<Vector<Literal>>();
		while (true)
		{

		  Collections.sort(knowledge_base, new Compare_knowledge_base());

		  Vector<Vector<Literal>> output = new Vector<Vector<Literal>>();

		  for (int i = 0;i < knowledge_base.size();i++)
		  {
			  
			  Vector<Literal> literal1 = knowledge_base.get(i);

		  if (knowledge_base.get(i).size() == 1)
		  {
			for (int j = i + 1;j < knowledge_base.size();j++)
			{
				
				Vector<Literal> literal2 = knowledge_base.get(j);
			  output = resolution(literal1, literal2);
			  if (output.size() != 0)
			  {
	            if(equals(query, literal1) || equals(query, literal2))
	                is_query_used = true;
			  }
			  if (isEmpty(output))
			  {
	            if(!is_query_used)
	            {
	                continue;
	            }
				return true;
			  }
			  resolved_clause = union(resolved_clause, output);
			}
		  }
		  }
			if (checkNewClauses(resolved_clause, knowledge_base))
			{
			  return false;
			}
			knowledge_base = union(knowledge_base, resolved_clause);
		}


	}
	public static Tree removeNot(Tree root, Tree parent, int check, boolean skip)
	{

	  if (root == null)
	  {
		  return root;
	  }

	  //Tree p = null;
	  //Tree old_root;
	  int left_right = 0;
	  if (parent == root)
	  {
		//p = parent;
		left_right = -1;
	  }
	  else
	  {
		  //p = (parent.child_2 == root ? parent.child_2 : parent.child_1);
		  left_right = (parent.child_2 == root ? 1 : 0);
	  }

	  if (root.Literal.name.charAt(0) == '~' || skip)
	  {
		if (!skip)
		{

			//old_root = root;
			root = root.child_2;
			//old_root = null;
		}

		if (root.Literal.name.charAt(0) == '~')
		{

			  //p  = root.child_2;
			  if(left_right == 0)
				  parent.child_1 = root.child_2;
			  else if(left_right == -1)
				  parent = root.child_2;
			  else
				  parent.child_2 = root.child_2;
			  //root = null;
			  root = root.child_2;
			  removeNot(root, parent, 0, false);
		}
		else if (root.Literal.category == 5)
		{

			  //p  = root;
			  if(left_right == 0)
				  parent.child_1 = root;
			  else if(left_right == -1)
				  parent = root;
			  else
				  parent.child_2 = root;
			  root.Literal.negation = true;
		}
		else
		{
			if (root.Literal.name.charAt(0) == '&')
			{
			  root.Literal.name = "|";
			}
			else
			{
				root.Literal.name = "&";
			}

			//p  = root;
		  if(left_right == 0)
		  {
			  parent.child_1 = root;
		  }
		  else if(left_right == -1)
		  {
			  parent = root;
		  }
		  else
		  {
			  parent.child_2 = root;
		  }

			if (root.child_1.Literal.category == 5)
			{
			  root.child_1.Literal.negation = true;
			}
			else
			{
				 removeNot(root.child_1, root, 1, true);
			}

			if (root.child_2.Literal.category == 5)
			{
			  root.child_2.Literal.negation = true;
			}
			else
			{
				 removeNot(root.child_2, root, 1, true);
			}
		}
	  }

	  removeNot(root.child_1, root, 0, false);
	  removeNot(root.child_2, root, 0, false);
	  return parent;
	}


	public static void andElimination(Tree root, Vector<Tree> clauses, int check)
	{
	  if (root.Literal.category == 3 && root.Literal.name.charAt(0) == '&')
	  {
		if (root.child_1.Literal.name.charAt(0) == '&')
		{
			andElimination(root.child_1, clauses, 0);
		}
		else
		{
			clauses.add(root.child_1);
		}

		if (root.child_2.Literal.name.charAt(0) == '&')
		{
			andElimination(root.child_2, clauses, 0);
		}
		else
		{
			clauses.add(root.child_2);
		}
	  }

	  else
	  {
		  clauses.add(root);
	  }

	}
	
	public static Tree replaceOp(Stack<Tree> stack)
	{
		return stack.lastElement();
	}
	
	public static Stack<Tree> removeTree(Stack<Tree> stack)
	{
		stack.pop();
		return stack;
	}
	
	public static Stack<Tree> pushTree(Stack<Tree> stack, Tree item)
	{
		stack.push(item);
		return stack;
	}
	
	public static Tree createTree(Vector<Literal> sentence)
	{

	Tree root;
	Stack<Tree> tree_stack = new Stack<Tree>();
	for (Literal Literal : sentence)
	{
		int temp = Literal.category;
		switch(temp)
		{
		case 5:
			root = new Tree(Literal);
			tree_stack = pushTree(tree_stack, root);
			break;
		default:
			root = new Tree(Literal);
			root.child_2 = replaceOp(tree_stack);
			tree_stack = removeTree(tree_stack);
			if (temp == 3)
			{
			  root.child_1 = replaceOp(tree_stack);
			  tree_stack = removeTree(tree_stack);
			}
			tree_stack = pushTree(tree_stack, root);
		}
	}

	root = replaceOp(tree_stack);
	tree_stack.pop();
	return root;

	}

	public static Literal replaceLiteral(Stack<Literal> stack)
	{
		return stack.lastElement();
	}
	
	public static Stack<Literal> removeLiteral(Stack<Literal> stack)
	{
		stack.pop();
		return stack;
	}
	
	public static Stack<Literal> pushLiteral(Stack<Literal> stack, Literal item)
	{
		stack.push(item);
		return stack;
	}

	public static Vector<Literal> convertToPostFix(Vector<Literal> exp)
	{
	  Stack<Literal> stack = new Stack<Literal>();
	  Vector<Literal> result = new Vector<Literal>();
	  for (Literal Literal : exp)
	  {
		  
		  if(Literal.category == 5)
		  {
			  result.add(Literal);
		  }
		  else if(Literal.category == 1)
		  {
			  stack = pushLiteral(stack, Literal);
		  }
		  else if(Literal.category == 2)
		  {
			  while (!stack.empty())
			  {
				Literal temp = replaceLiteral(stack);
				stack = removeLiteral(stack);
				if (temp.category == 1)
				{
				  break;
				}
				else
				{
					result.add(temp);
				}
			  }
		  }
		  else if(Literal.category == 4 || Literal.category == 3)
		  {
			  while (!stack.empty() && precedence(Literal.name.charAt(0)) <= precedence(stack.lastElement().name.charAt(0)))
			  {
					result.add(replaceLiteral(stack));
					stack = removeLiteral(stack);
			  }
			  stack.push(Literal);
		  }

	  }

	  while (!stack.empty())
	  {
	  result.add(replaceLiteral(stack));
	  stack = removeLiteral(stack);
	  }
	return result;
	}

	public static Vector<Literal> convertToLiterals(String sentence)
	{
	  Vector<Literal> sen = new Vector<Literal>();
	  for (int i = 0;i < sentence.length();i++)
	  {
		  Literal Literal = new Literal();
		  if (sentence.charAt(i) == '(')
		  {
			Literal.name = "(";
			Literal.category = 1;
		  }
		  else if (sentence.charAt(i) == ')')
		  {
			Literal.category = 2;
			Literal.name = ")";
		  }
		  else if (sentence.charAt(i) == '~')
		  {
				Literal.name = "~";
				Literal.category = 4;

		  }
		  else if (Character.isUpperCase(sentence.charAt(i)))
		  {
			int position = sentence.indexOf(")",i);
			String q = sentence.substring(i, position + 1);

			Literal = getLiterals(q);
			i = position;
			Literal.category = 5;

		  }
		  else
		  {
			  Literal.category = 3;
			  if (sentence.charAt(i) == '=')
			  {
				  Literal.name = sentence.substring(i, i + 2);
				  i = i+1;
			  }
			  else
			  {
				  Literal.name = Character.toString(sentence.charAt(i));
			  }
		  }
		  sen.add(Literal);
	  }
		return sen;
	}

	public static void getInput() throws IOException
	{
	  FileReader reader = new FileReader("input.txt");
      BufferedReader bufferedReader = 
              new BufferedReader(reader);
	  int no_of_queries;
	  String temp;

	  temp = bufferedReader.readLine();
	  no_of_queries = Integer.parseInt(temp);

	  for (int i = 0;i < no_of_queries;i++)
	  {
		  String query;
		  query = bufferedReader.readLine();
		query = query.replaceAll("\\s+","");
		if (query.length() > 0)
		{
		  queries.add(getLiterals(query));
		}
	  }

	  int no_of_sentences;
	  temp = bufferedReader.readLine();
	  no_of_sentences = Integer.parseInt(temp);
	  Vector<Tree> clauses = new Vector<Tree>();

	  for (int i = 0;i < no_of_sentences;i++)
	  {
		  String sentence;
		  sentence = bufferedReader.readLine();
		  sentence = sentence.replaceAll("\\s+","");

		if (sentence.length() == 0)
		{
			continue;
		}
		Vector<Literal> kb_sentence = convertToLiterals(sentence);

		Vector<Literal> literals = convertToPostFix(kb_sentence);
		Tree root = createTree(literals);
		root = removeNot(root, root, 0, false);

//		sen1.clear();
//		res.clear();
		
		andElimination(root, clauses, 0);
		
	  }
		for (Tree root : clauses)
		{
		  Vector<Literal> clause = new Vector<Literal>();
		  fromTreeToClause(root, clause);
		  standardise(clause);

		  knowledge_base.add(clause);

		}

	reader.close();
	}



	public static variable_unify variableUnify(var variable, expr value, variable_unify mapping)
	{
	String index_val = null;

	
	switch(value.category)
	{
	case 1:
		index_val = ((constant)value).value;
		break;
	case 2:
		index_val = ((var)value).value;
		break;
	}



	String variable_value = variable.value;
	if (mapping.variable_val.containsKey(variable_value))
	{
	  String val = mapping.variable_val.get(variable_value);
	  if(val.equals(index_val))
	  {
		  return mapping;
	  }
	  if(Character.isLowerCase(val.charAt(0)))
	  {
		  var new_var = new var(val);
		  return doUnification(new_var, value, mapping);
	  }
	  else
	  {
		  constant new_const = new constant(val);
		  return doUnification(new_const, value, mapping);
	  }
	}

	if (mapping.variable_val.containsKey(index_val))
	{
		  String val = mapping.variable_val.get(index_val);
		  if(val.equals(variable_value))
		  {
			  return mapping;
		  }
		  if(Character.isLowerCase(val.charAt(0)))
		  {
			  var new_var = new var(val);
			  return doUnification(new_var, value, mapping);
		  }
		  else
		  {
			  constant new_const = new constant(val);
			  return doUnification(new_const, value, mapping);
		  }
	}
	mapping.variable_val.put(variable_value, index_val);

	return mapping;

	}

	
	public static void main(String[] args) throws IOException
	{

	  getInput();

	Vector<Vector<Literal>> OLD_knowledge_base = new Vector<Vector<Literal>>();
	for (Vector<Literal> clause : knowledge_base)
	{
	  OLD_knowledge_base.add(clause);
	}
	
    FileWriter fileWriter =
            new FileWriter("output.txt");
    BufferedWriter bufferedWriter =
            new BufferedWriter(fileWriter);

	  for (Literal a : queries)
	  {
		Vector<Literal> query = new Vector<Literal>();

		query.add(a);
		boolean answer = isTrue(query);
//		System.out.print("result_query=");
//		System.out.print(answer);
//		System.out.print("\n");

		if (answer)
		{
		bufferedWriter.write("TRUE");
		bufferedWriter.newLine();
		}
		else
		{
			bufferedWriter.write("FALSE");
			bufferedWriter.newLine();
		}

//		System.out.print(answer);
//
//		System.out.print("\n");
	  knowledge_base.clear();
	  for (Vector<Literal> clause : OLD_knowledge_base)
	  {
		knowledge_base.add(clause);
	  }
	  }
	  bufferedWriter.close();
	  fileWriter.close();
	}

}
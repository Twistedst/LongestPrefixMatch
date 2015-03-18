import java.util.*;
import java.io.*;

/******
*	LongestPrefixMatch.java
*	
*	@author Steven Hawley
*	@version 3/16/2015
*******/

public class Trie {

private static Node root;
//private int numNodes;

//private int numPrefixes;
	
	
	public Trie()
	{
		root = new Node();
	}
	
	
	//Converts IP Address to decimal
	private static String ipToBitString(String ipAddr)
	{		
		long result = 0;
		
		String[] ipArray = ipAddr.split("\\.");
		
		for (int i = 3; i >= 0; i--)
		{
			long ip = Long.parseLong(ipArray[3-i]);
			
			result |= ip << (i * 8);
		}
		
		return binaryForm(result);
		
	}
	
	//Converts Decimal number to binary (String)
	private static String binaryForm(long number)
	{
		if (number == 0)
		{
			return "0";
		}
		
		String binary = "";
		
		while (number > 0)
		{
			long rem = number % 2;
			binary = rem + binary;
			number = number / 2;
		}
		return binary;
	}
	
	//Count the number of spaces in the path
/* 	public int countPathLength( String path )
	{
		return path.length() - path.replaceAll(" ", "").length();
	} */
		
	public class Node
	{
		Node[] children;
		String prefix;
		String nextHop;
		
		int as;
		
		public Node()
		{
			children = new Node[2];
			prefix = null;
			nextHop = null;
		}
		public Node(String prefix, int as, String nextHop)
		{
			children = new Node[2];
			this.prefix = prefix;
			this.nextHop = nextHop;
			this.as = as;
		}
		
		public void upDateNode(String prefix, int as, String nextHop)
		{
			this.prefix = prefix;
			this.nextHop = nextHop;
			this.as = as;
		}
		
		public void setPrefix(String prefix)
		{
			this.prefix = prefix;
		}
		
		public void setNextHop(String nextHop)
		{
			this.nextHop = nextHop;
		}
		
		public String getPrefix()
		{
			return prefix;
		}
		
		public String getNextHop()
		{
			return nextHop;
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		Trie x = new Trie();
		
		String routes, address;		
		routes = args[0];
		address = args[1];
		
		Node bestNode = new Node();
		
		/* Initialize the trie */
		try(BufferedReader br = new BufferedReader( new FileReader(routes)))
		{
			String prefix, nextHop, prefix2, nextHop2;
			int pathLength, pathLength2;
			
			
			/* Find shortest path for ip address */
			for(String line; (line = br.readLine()) != null; ) 
			{				
				String[] parsedLine = line.split("|");
							
				prefix = parsedLine[0];
				pathLength = parsedLine[1].length() - parsedLine[1].replaceAll(" ", "").length();
				nextHop = parsedLine[2];
				
				bestNode.upDateNode(prefix, pathLength, nextHop);
				
				
								
				for(String line2; (line2 = br.readLine()+1) != null; )
				{
					String[] parsedLine2 = line2.split("|");
				
					prefix2 = parsedLine2[0];
					pathLength2 = parsedLine2[1].length() - parsedLine2[1].replaceAll(" ", "").length();
					nextHop2 = parsedLine2[2];
					
					if(prefix.equals(prefix2))
					{
						//Update bestNode if better path is found for ipAddr
						if(pathLength <= pathLength2)
						{
							bestNode.upDateNode(prefix2, pathLength2, nextHop2);
							break;
						}
					}			
				}
			}
			
			String ipAddr = ipToBitString(bestNode.getPrefix());
			char[] ipArray = ipAddr.toCharArray();
			
			//current node used to go through tree while searching for empty node
			Node currNode = root;
			
			//Go through bit string of ipAddr and find next open node
			for(int i = 0; i <= ipAddr.length(); i++)
			{
				if( currNode.children[ ipArray[i]] == null)
				{
					currNode.children[ ipArray[i] ] = bestNode;
					System.out.println(currNode.getPrefix());
					break;
				}
				else
				{
					currNode = currNode.children[ipArray[i]];
				}
			}
		}
		
		System.out.println("\nLongest Prefix Match");
		System.out.println("\n\n");
		
		/* Longest Prefix Match  */
		try(BufferedReader bre = new BufferedReader( new FileReader(address)))
		{
			String ipAddr;
			//Read line by line
			for(String line; (line = bre.readLine()) != null; )
			{
				ipAddr = ipToBitString(line);
				char[] ipArray = ipAddr.toCharArray();
				
				Node currNode = root;
				
				for(int i = 0; i <= ipAddr.length(); i++)
				{
					if( currNode.children[ ipArray[i] ] == null)
					{
						System.out.println(ipAddr + "	" + currNode.getPrefix());
						break;
					}
					else
					{
						currNode = currNode.children[ ipArray[i] ]; 
					}
					//Say No Match somehow
				}
			}
		}
		
		
	}
	
	
}

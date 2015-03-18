import java.util.*;
import java.util.zip.GZIPInputStream;
import java.io.*;

/******************************
*	LongestPrefixMatch
*	
*	@author Steven Hawley
*	@version 3/16/2015
********************************/

public class Trie {

private static Node root;	
	
	public Trie()
	{
		root = new Node();
	}
	
	
	/**
	 * Converts IP Address to decimal using the method binaryForm
	 * @param ipAddr
	 * @return
	 */
	private static String ipToBitString(String ipAddr)
	{		
		long result = 0;
		//String[] split = ipAddr.split( "\" );
		//String[] ipArray = split[0].split("\\.");
		String[] ipArray = ipAddr.split("\\.");
		
		for (int i = 3; i >= 0; i--)
		{
			long ip = Long.parseLong(ipArray[3-i]);
			
			result |= ip << (i * 8);
		}
		
		return binaryForm(result);
		
	}
	
	/**********************************************
	 * Converts Decimal number to binary (String)
	 * 
	 * @param number
	 * @return 
	 **********************************************/
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
	
	/** ***********************************************
	 * 
	 * Search for prefix match using a binary search 
	 * 
	 * ************************************************/
	private Node search( Node n, String line)
	{
		String ipAddr = ipToBitString(line);
		
		for(int i = 0; i <= ipAddr.length(); i++)
		{
			char bit = ipAddr.charAt(i);
			
			if(bit == '0')
			{
				if(n.children[0].nextHop == "")
				{
					n.children[0].nextHop = n.nextHop;
					break;
				}else
				{
					n = n.children[0];
				}
			}else 
				if(bit == '1')
				{
					if(n.children[1].nextHop == "")
					{
						n.children[1].nextHop = n.nextHop;
						break;
					}else
					{
						n = n.children[1];
					}
					
				}				
		}
		
		return n;
	}

	/***********************************************************************************
	 * Search through routes file for bestNode which is the node with the shortest path.
	 * 		Only compares prefixes with same ip addresses.
	 * 
	 * @param line
	 * @param line2
	 * @return bestNode
	 ***********************************************************************************/
	private Node findBestNode(String line, String line2)
	{
		Node bestNode = new Node();
		int pathLength, pathLength2;
		String prefix, nextHop, prefix2, nextHop2;
		
		String[] parsedLine = line.split("\\|");
		
		prefix = parsedLine[0];
		pathLength = parsedLine[1].length() - parsedLine[1].replaceAll(" ", "").length();
		nextHop = parsedLine[2];
		
		bestNode.upDateNode(prefix, pathLength, nextHop);	
						
			String[] parsedLine2 = line2.split("|");
		
			prefix2 = parsedLine2[0];
			pathLength2 = parsedLine2[1].length() - parsedLine2[1].replaceAll(" ", "").length();
			nextHop2 = parsedLine2[2];
			
			if(prefix.equals(prefix2))
			{
				//Update bestNode if better path is found for ipAddr
				if(pathLength2 <= pathLength)
				{
					bestNode.upDateNode(prefix2, pathLength2, nextHop2);
					return bestNode;
				}
			}
			return bestNode;
		
	}
	
	/****************************************************
	 * Node class used to create trie and store prefixes
	 * 
	 *
	 ****************************************************/
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
		
//		if ( args.length < 2 )
//		{
//			System.out.println( "Please input 2 files of <routes> and <addresses>" );
//			return;
//		}
		
		Trie x  = new Trie();
		Node bestNode = root;
		
//		String routes, address;		
//		routes = args[0];
//		address = args[1];
		
		
		/** Initialize the trie **/
		
		try ( BufferedReader br = new BufferedReader(new FileReader("/home/hawleyst/workspace/LongestPrefixMatch/src/routes.txt")))
				{
			
			/** Find ip with shortest path (bestNode) **/
			
			for(String line; (line = br.readLine()) != null; ) 
			{				
				for(String line2; (line2 = br.readLine()+1) != null; )
				{
					String[] parsedLine = line.split("\\|");
					String[] parsedLine2 = line2.split("\\|");
					if(parsedLine[0].equals(parsedLine2[0]))
					{
						//System.out.println("line1: " + line);
						//System.out.println("line2: " + line2);
						bestNode = x.findBestNode(line, line2);
						//System.out.println(bestNode.getNextHop());
						
					}
					else
					{
						break;
					}
										
				}
				//System.out.println("bestNode IP: " + bestNode.getPrefix());
			}
			
			String ipAddr = ipToBitString(bestNode.getPrefix());
			
			/** Insert bestNode into trie **/
			
			//current node used to go through tree while searching for empty node
			Node currNode = root;
			
			//Go through bit string of ipAddr and find next open node
			for(int i = 0; i <= ipAddr.length(); i++)
			{
				if( currNode.children[ ipAddr.charAt(i) ] == null)
				{
					currNode.children[ ipAddr.charAt(i) ] = bestNode;
					System.out.println(currNode.getPrefix());
					break;
				}
				else
				{
					currNode = currNode.children[ipAddr.charAt(i)];
				}
			}
		} catch ( IOException e)
		{
			System.out.println( "There was a problem reading the routes file" );
			return;
		}
		
		
		/* Longest Prefix Match  */
		
		System.out.println("\n\nLongest Prefix Match");
		System.out.println("\n");	
		
		try ( BufferedReader bre = new BufferedReader(new FileReader("/home/hawleyst/workspace/LongestPrefixMatch/src/addresses.txt")))
		{
			//Read line by line
			for(String line; (line = bre.readLine()) != null; )
			{
				Node match = null;
				match = x.search(root, line);
				if(match == null)
				{
					System.out.println(line + "      " + "No Match");
					break;
				}
				System.out.println(line + "     " + match.getPrefix());
				
			}
		} catch ( IOException e )
		{
			System.out.println( "There was a problem reading the addresses file" );
			return;
		}
		
		
		/* Test ipToBitString and binaryForm */
		
		//String testIP = "1.0.4.0\24";
//		String testIP = "1.0.4.0";
//		String bitStringTest = "";
//		bitStringTest = ipToBitString(testIP);
//		System.out.println(bitStringTest);
		
	}
	
	
}

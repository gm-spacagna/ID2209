package hw1.agents.profiler;

import hw1.agents.curator.CArtifact;
import hw1.agents.curator.CArtifacts;
import hw1.art.ArtisticGenre;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class represents user object, contains personal data based on which the
 * profiler agent will look for interesting art objects. The class also creates
 * logic for generating a random user with occasional interests.
 * 
 * @author veronika
 * 
 */
public class CUser implements Serializable
{
	// /// values for random generation of a user //////
	private enum GENDER
	{
		male, female;
	}

	private enum NAME
	{
		Peter, James, Maria, Bob, Erika, Ljudvig, Tina, Barbara;
	}

	private enum OCCUPATION
	{
		student, engineer, artist, retired, scientist, jobless, doctor;
	}

	// ////////////////////////////////////////////////////////////
	String name;
	String age;
	String occupation;
	String gender;
	List<String> interests;
	List<Integer> visitedIds;

	public CUser(String name, String age, String occupation, String gender,
			List<String> interests, List<Integer> visited)
	{
		this.name = name;
		this.age = age;
		this.occupation = occupation;
		this.gender = gender;
		this.interests = interests;
		this.visitedIds = visited;
	}

	public String toString()
	{
		return ("\nName: " + name + "\n" + "Age: " + age + "\n" + "Occupation:"
				+ occupation + "\n" + "Gender: " + gender + "\n"
				+ "Interests: " + interests.toString());
	}

	public String getAge()
	{
		return age;
	}

	/*
	 * public Occupation getOccupation() { return occupation; }
	 */

	/*
	 * public Gender getGender() { return gender; }
	 */

	public List<String> getListOfInterests()
	{
		return interests;
	}

	public List<Integer> getVisitedIds()
	{
		return visitedIds;
	}

	public void addVisited(Integer object)
	{
		getVisitedIds().add(object);
	}

	/**
	 * Function generates a user with a random set of parameters.
	 * 
	 * @return
	 */
	public static CUser generateRandomUser()
	{
		Random r = new Random();

		String age = 20 + r.nextInt(50) + ""; // ages between 10 and 70
		GENDER gender = GENDER.values()[(r.nextInt(GENDER.values().length))];
		NAME name = NAME.values()[(r.nextInt(NAME.values().length))];
		OCCUPATION occupation = OCCUPATION.values()[(r.nextInt(OCCUPATION
				.values().length))];
		List<String> interests = generateRandomInterests();
		List<Integer> visitedIds = pickVisitedArtifacts();

		return new CUser(name.toString(), age, occupation.toString(),
				gender.toString(), interests, visitedIds);
	}

	private static List<Integer> pickVisitedArtifacts()
	{
		List<Integer> pickedArtifacts = new ArrayList<Integer>();
		Random r = new Random();
		for (int i = 0; i < 3; i++)
		{
			CArtifact artifact = CArtifacts.LIST.get(r.nextInt(CArtifacts.LIST
					.size()));
			if (!pickedArtifacts.contains(artifact.getId()))
			{
				pickedArtifacts.add(artifact.getId());
			}
		}
		return pickedArtifacts;
	}

	private static List<String> generateRandomInterests()
	{
		List<String> pickedInterests = new ArrayList<String>();
		Random r = new Random();
		int nAmountOfInterests = 1 + r
				.nextInt(ArtisticGenre.values().length - 1);// at
																// least
																// one
																// interest
		for (int i = 0; i < nAmountOfInterests; i++)
		{
			ArtisticGenre interest = ArtisticGenre.values()[(r
					.nextInt(ArtisticGenre.values().length))];
			if (!pickedInterests.contains(interest.toString()))
			{
				pickedInterests.add(interest.toString());
			}
		}
		return pickedInterests;
	}
}
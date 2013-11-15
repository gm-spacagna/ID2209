package hw1.agents.curator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CArtifact implements Serializable
{
	private Integer id;
	private String name;
	private String creator;
	private String dateOfCreation;
	private String placeOfCreation;
	private String genre;

	public CArtifact(Integer id, String name, String creator,
			String dateOfCreation, String placeOfCreation, String genre)
	{
		this.id = id;
		this.name = name;
		this.creator = creator;
		this.dateOfCreation = dateOfCreation;
		this.placeOfCreation = placeOfCreation;
		this.genre = genre;
	}

	public Integer getId()
	{
		return id;
	}

	public String getGenre()
	{
		return genre;
	}

	public String toString()
	{
		return ("ARTIFACT: " + "ID: " + id + "\n" + "Name: " + name + "\n"
				+ "Creator " + creator + "\n" + "Date of creation: "
				+ dateOfCreation + "\n" + "Place of creation: "
				+ placeOfCreation + "\n" + "Genre: " + genre + "\n");
	}

	public static List<CArtifact> getArtifactsByIDs(List<Integer> ids)
	{
		List<CArtifact> list = new ArrayList<CArtifact>();
		for (CArtifact a : CArtifacts.LIST)
		{
			if (ids.contains(a.id))
			{
				list.add(a);
			}
		}
		return list;
	}

	public static List<CArtifact> getRandomListOfArtifacts()
	{
		Random r = new Random();
		List<CArtifact> randomArtifacts = new ArrayList<CArtifact>();
		for (int i = 0; i < 5; i++)
		{
			int n = r.nextInt(CArtifacts.LIST.size());
			if (!randomArtifacts.contains(CArtifacts.LIST.get(n)))
			{
				randomArtifacts.add(CArtifacts.LIST.get(n));
			}
		}
		return randomArtifacts;
	}
}
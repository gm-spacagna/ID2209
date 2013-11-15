package hw1.agents.curator;

import hw1.art.ArtisticGenre;

import java.util.ArrayList;
import java.util.List;

public class CArtifacts {
  // used also in class CUser for random user generation,
  // for making a random list of already visited objects
  public static List<CArtifact> LIST = getList();

  static List<CArtifact> getList() {
    List<CArtifact> list = new ArrayList<CArtifact>();

    list.add(new CArtifact(1, "Mona Lisa", "Leonardo da Vinci",
        "cirka 1503-1519", "Italy", ArtisticGenre.PAINTING.toString()));
    list.add(new CArtifact(2, "Sunflowers", "Vincent van Gogh", "1888",
        "France", ArtisticGenre.PAINTING.toString()));
    list.add(new CArtifact(3, "Liberty Leading the People", "Eugene Delacroix",
        "1830", "France", ArtisticGenre.PAINTING.toString()));
    list.add(new CArtifact(4, "Dying Slave", "Michelangelo", "1513-1516",
        "Italy", ArtisticGenre.SCULPTURE.toString()));
    list.add(new CArtifact(5, "Elephant drinking", "Nick Brandt", "2011",
        "Africa", ArtisticGenre.PHOTOGRAPHY.toString()));
    list.add(new CArtifact(6, "War and piece", "Lev Tolstoi", "1869", "Russia",
        ArtisticGenre.LITERATURE.toString()));
    list.add(new CArtifact(7, "The Shawshank Redemption", "Frank Darabont",
        "1994", "USA", ArtisticGenre.CINEMA.toString()));
    list.add(new CArtifact(8, "Family group", "Henry Moor", "1950", "England",
        ArtisticGenre.SCULPTURE.toString()));
    list.add(new CArtifact(9, "Art Nouveau Brooch", "HENRY VAN DE VELDE",
        "1950", "Holland", ArtisticGenre.JEWELRY.toString()));
    list.add(new CArtifact(10, "The Beatles", "Beatles", "1968", "England",
        ArtisticGenre.MUSIC.toString()));
    list.add(new CArtifact(11, "Honey is Sweeter than Blood", "Salvador Dali",
        "1941", "Spain", ArtisticGenre.PAINTING.toString()));
    list.add(new CArtifact(12, "The Catcher in the Rye", "J.D.Salinger",
        "1951", "USA", ArtisticGenre.LITERATURE.toString()));
    list.add(new CArtifact(13, "The Basket of Bread and Girl from Figueres",
        "Salvador Dali", "1926", "Spain", ArtisticGenre.PAINTING.toString()));
    list.add(new CArtifact(14, "Wassily chair", "Marcel Breuer", "1920s",
        "German", ArtisticGenre.FURNITURE.toString()));
    list.add(new CArtifact(15, "Narva castle and bastions", "",
        "14th -17th century", "Estonia", ArtisticGenre.ARCHITECTURE.toString()));
    list.add(new CArtifact(16, "Chanel hat", "1921", "Coco Chanel", "France",
        ArtisticGenre.FASHION.toString()));
    list.add(new CArtifact(17, "Second World War Exibition", "1941-1945", "",
        "World wide", ArtisticGenre.HISTORY.toString()));
    return list;
  }
}
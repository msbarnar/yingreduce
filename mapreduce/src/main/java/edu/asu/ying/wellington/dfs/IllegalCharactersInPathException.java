package edu.asu.ying.wellington.dfs;

import java.util.Collection;

/**
 *
 */
public class IllegalCharactersInPathException extends InvalidPathException {

  private Collection<Character> characters;

  public IllegalCharactersInPathException(String path, Collection<Character> characters) {
    super(path);
    this.characters = characters;
  }

  public Collection<Character> getCharacters() {
    return characters;
  }
}

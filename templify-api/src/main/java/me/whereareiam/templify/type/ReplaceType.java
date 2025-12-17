package me.whereareiam.templify.type;

/**
 * Determines how a replacement value is chosen for a placeholder occurrence.
 */
public enum ReplaceType {
  /**
   * Always use the first value in the list.
   */
  FIRST,

  /**
   * Pick a random value for each occurrence.
   */
  RANDOM,

  /**
   * Cycle through the values in order (wrap-around).
   */
  SEQUENTIAL,

  /**
   * Choose a value based on matching condition rules.
   */
  CONDITIONAL
}



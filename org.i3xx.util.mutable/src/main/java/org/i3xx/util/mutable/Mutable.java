package org.i3xx.util.mutable;

/**
 * The mutable object is designed as a container to hold a mutable value. The preferred
 * use is as a value in a collection. The mutable policy needs a separate look at the
 * hashCode and equals behavior.
 * 
 * Caution:
 * Do not use a mutable object in a hash set or map as a key. This will not work
 * properly and has unexpected side effects.
 * 
 * Two mutable objects are equal if both values are equal.
 * The hash code of a mutable object is the hash code of it's value.
 * 
 * @author Stefan
 *
 */
public @interface Mutable {

}

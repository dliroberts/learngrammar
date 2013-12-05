/**
 * This package contains classes for dealing with syntax and realisation. 
 * The basic objects handled by the realiser are referred
 * to as <code>Spec</code>s and are extensions of the abstract {@link simplenlg.realiser.Spec} class.
 * 
 *  <P>
 *  A <code>Spec</code> is either a syntactic phrase, a string,
 *   or a higher-level collection of phrases and/or strings, such as a paragraph.
 *   Any such higher-level collections is a {@link simplenlg.realiser.TextSpec}. Syntactic phrases,
 *   on the other hand, extend the <code>Spec</code> class, but also implement a 
 *   {@link simplenlg.realiser.Phrase} interface. Various classes are included to represent
 *   different kinds of syntactic phrase in the major grammatical categories, as well as
 *   coordinate phrases, which implement the {@link simplenlg.realiser.CoordinatePhrase} interface.
 *   
 *   <P>
 *   Realisation of a <code>Spec</code> involves converting the tree structure representing by
 *   the <code>Spec</code> into a linguistic string, with or without formatting. The realisation
 *   is handled by the {@link simplenlg.realiser.Realiser} class.
 *   
 *   <P>
 *   Syntactic operations which have inflectional consequences (such as pluralisation and number
 *   agreement) are handled automatically. This is where the <code>simplenlg.realiser</code>
 *   and the <code>microplanner.lexicon</code> packages interact.
 *   
 * 
 * @author Albert Gatt
 * @author Ehud Reiter
 * 
 */
package simplenlg.realiser;
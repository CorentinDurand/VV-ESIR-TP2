
# Using PMD


Pick a Java project from Github (see the [instructions](../sujet.md) for suggestions). Run PMD on its source code using any ruleset (see the [pmd install instruction](./pmd-help.md)). Describe below an issue found by PMD that you think should be solved (true positive) and include below the changes you would add to the source code. Describe below an issue found by PMD that is not worth solving (false positive). Explain why you would not solve this issue.


## Answer

Dans https://github.com/apache/commons-collections/blob/master/src/main/java/org/apache/commons/collections4/CollectionUtils.java, ligne 1092, PMD détecte une 
erreur true positive : PreserveStackTrace:	Thrown exception does not preserve the stack trace of exception 'ex' on all code paths. 

```java
} catch (final IllegalArgumentException ex) {     
          throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());  
      }
```
     
Cette règle signale les cas où une exception est capturée et relancée sans que la trace de la pile d'origine ne soit conservée et peut donc causer des erreurs de debugging. On peut donc corriger cette erreur en transmettant l'exception capturée ex comme second argument à la nouvelle exception afin de préserver la trace de la pile originale :

```java
`} catch (final IllegalArgumentException ex) {
          throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName(), ex);  
      }`
```

Toujours dans le même projet, dans https://github.com/apache/commons-collections/blob/master/src/main/java/org/apache/commons/collections4/list/AbstractLinkedList.java,  on trouve un problème faux positif\
ligne 154. En effet, PMD détecte l'erreur suivante : ompareObjectsWithEquals:	Use equals() to compare object references.

```java
`@Override
  public boolean hasNext() {
    return next != parent.header;
    }
```

La comparaison next != parent.header vérifie si l'itération a atteint le dernier nœud (la fin de la liste), et non si les données au sein de deux nœuds sont égales. L'utilisation de equals() serait inappropriée ici, car on ne cherche pas à comparer le contenu des nœuds, mais à s'assurer que l'itération continue dans la liste et qu'on n'a pas atteint la fin.





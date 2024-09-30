# TCC *vs* LCC

Explain under which circumstances *Tight Class Cohesion* (TCC) and *Loose Class Cohesion* (LCC) metrics produce the same value for a given Java class. Build an example of such as class and include the code below or find one example in an open-source project from Github and include the link to the class below. Could LCC be lower than TCC for any given class? Explain.

A refresher on TCC and LCC is available in the [course notes](https://oscarlvp.github.io/vandv-classes/#cohesion-graph).

## Answer

TCC et LCC produisent la même valeur dans deux cas différents. Le premier lorsque toutes les méthodes d'une classe sont directement connectées entre elles (TCC = LCC = 1). Mais aussi lorsque aucune des méthodes ne sont reliées entre elles (TCC = LCC = 0).

Par ailleurs, la valeur de LCC ne peut pas être plus basse que la valeur de TCC. En effet, le numérateur de TCC se traduit par le nombre de paires directement connectées alors que le numérateur de LCC est le nombre de paires directement OU indirectement connectées. Le dénominateur est le même pour les deux métriques (nombre total de paires de méthodes). Ainsi, LCC ne pourra jamais être inférieur à TCC.



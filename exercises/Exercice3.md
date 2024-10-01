# Extending PMD

Use XPath to define a new rule for PMD to prevent complex code. The rule should detect the use of three or more nested `if` statements in Java programs so it can detect patterns like the following:

```Java
if (...) {
    ...
    if (...) {
        ...
        if (...) {
            ....
        }
    }

}
```
Notice that the nested `if`s may not be direct children of the outer `if`s. They may be written, for example, inside a `for` loop or any other statement.
Write below the XML definition of your rule.

You can find more information on extending PMD in the following link: https://pmd.github.io/latest/pmd_userdocs_extending_writing_rules_intro.html, as well as help for using `pmd-designer` [here](./designer-help.md).

Use your rule with different projects and describe you findings below. See the [instructions](../sujet.md) for suggestions on the projects to use.

## Answer

Après avoir appliqué notre règle à divers projets, nous constatons qu'elle fonctionne correctement, comme le montre l'exemple d'Apache Commons Collections.

Le code :

![image](https://github.com/user-attachments/assets/c1ab5702-fca9-4b58-995e-256dd3ef4276)

Le message :

![image](https://github.com/user-attachments/assets/ff005970-a011-469a-89d4-432b843675ba)


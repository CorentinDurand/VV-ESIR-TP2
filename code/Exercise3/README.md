# Code of your exercise

Put here all the code created for this exercise

<?xml version="1.0"?>

<ruleset name="myruleset" xmlns="http://pmd.sourceforge.net/ruleset/2.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd">

    <description>
        My custom rules set
    </description>


    <rule name="threeif" language="java" message="Three if in a row" class="net.sourceforge.pmd.lang.rule.xpath.XPathRule">
        <description>
            My custom rules set
        </description>
        <priority>3</priority>
        <properties>
            <property name="xpath">
                <value>
                    <![CDATA[//IfStatement[Block//IfStatement[Block//IfStatement]]]]>
                </value>
            </property>
        </properties>
    </rule>

</ruleset>

# MafiaGame

## Authors
Daniel Baeta - @dbaeta<br>
Samuel Laço - @SamLaco<br>
Ricardo Constantino - @wiiaboo<br>
Rodrigo Sanches - @Rodas1980<br>

Project developed during the 10th and 11th week of Academia de Código's 11th bootcamp (ENUMinatti)<br>

## Concepts used in the project
Concurrency/Working with threads™ (not learned during the project, but heavily used)<br>
Networking (we used only TCP sockets)<br>

## Concepts learned while and used in the project:

Software Engineering and Developing Methodologies<br>
Agile, Kanban and Lean (we sorta worked in Kanban style)<br>
Model View Controller<br>
JavaFX<br>
SceneBuilder<br>
Service Layer Pattern (we didn't use it, though we should've)<br>
Service Locator<br>
Relational Databases Management Systems<br>
Oracle MySQL<br>
Java Database Connector<br>
Maven<br>


## Instructions to play

The Mafia party game is usually played with a group of people and with someone controlling the flow of the game.<br>
In the start of the game, everyone is assigned a role and that role is aligned to a faction.<br>
<br>
Your objective is to win with your faction.<br>
If you're a Villager, you win when you purge your village of any Mafia.<br>
If you're Mafia, you win when your faction outnumbers the villagers.<br>
<br>
A round of the game is composed by Day and Night, each of these with an allocated time to Talk and Vote.<br>
During the day, everyone can talk and try to find out who's a probable Mafia.<br>
At the end of the day, there will be a Vote, at the end of which someone will be lynched.<br>
During the night, only the Mafia members gather in assembly, and the Villagers are asleep.<br>
<br>
The game ends when either faction wins or not enough players remain for the game to continue.<br>
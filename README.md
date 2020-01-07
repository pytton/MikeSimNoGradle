# MikeSimJava
Trading simulator using TWS API for paper trading. Test bed for custom algos.
package com.ib.* is needed to connect to Interactive Brokers API.


###TODO:

Bug in Scalper algo - negative intervals not protected. fix it.
Create control for simple order placement
Create control for stepper algo

1. pressing buttons in PositionsWindow launches algos. This is currently hardcoded.
Make something so that you can choose which algo is launched by column1, which by column2,
etc. Use a JavaFX ChoiceBox to choose the column - selecting a column should
change the contents of AnchorPane columnActionsAnchorPane in ControllerPositionsWindow.java
columnActionsAnchorPane should have another ChoiceBox inside it with all the available
algos listed. Choosing one of the Algos should change the contents of another Pane
below the choicebox depending on the Algo selected. columnActionsAnchorPane does not 
have to be an AnchorPane - it can be another kind of Pane. The Panes used should be
defined in an .fxml file for easy use with SceneBuilder.

#current commit:
Working on GUI. 
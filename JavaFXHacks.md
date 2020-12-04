making font fit more of button: padding:
button.setPadding(new Insets(0, 0, 0, 0));

    //        Button button = buttonTable.getButton(3,1);
    //
    //        button.setText("Hello!");
    //        button.setStyle("-fx-background-color: red");
    //
    //        buttonTable.getButton(3,2).setText("");
    //        buttonTable.getButton(3,3).setStyle("-fx-text-fill: green");
    //        buttonTable.getButton(4,0).setStyle("-fx-background-color: blue");
    //        buttonTable.getButton(4,1).setStyle("-fx-background-color: grey;-fx-border-color: black; -fx-border-width: 1px");
    //        buttonTable.getButton(5,1).setStyle("-fx-background-color: blue;-fx-border-color: black; -fx-border-width: 1px");
    //        buttonTable.getButton(5,1).setStyle("-fx-border-color: black; -fx-border-width: 1px;");
    
styles: https://docs.oracle.com/javafx/2/css_tutorial/jfxpub-css_tutorial.htm
color reference: https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html#introscenegraph
https://stackoverflow.com/questions/24702542/how-to-change-the-color-of-text-in-javafx-textfield

getting the stage of a node and closing the window it is in.
errorLabel is a name of a node in the window
https://www.udemy.com/course/advanced-programming-with-javafx-build-an-email-client/learn/lecture/16121739#questions:
    
        Stage stage = (Stage) errorLabel.getScene().getWindow();
        stage.close();

Thread safe singleton:


public static ThreadSafeSingleton getInstanceUsingDoubleLocking(){
    if(instance == null){
        synchronized (ThreadSafeSingleton.class) {
            if(instance == null){
                instance = new ThreadSafeSingleton();
            }
        }
    }
    return instance;
}

Custom components in SceneBuilder:
https://rterp.wordpress.com/2014/05/21/adding-custom-javafx-components-to-scene-builder-2-0/

https://rterp.wordpress.com/2013/09/13/creating-custom-javafx-components-with-scene-builder-and-fxml/

https://rterp.wordpress.com/2014/07/28/adding-custom-javafx-component-to-scene-builder-2-0-part-2/



import app.App;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;


/**
 *
 * @author omar
 */
public class main {
    public static void main(String[] args) {
        
        App app = new App();
        
        Scanner in = new Scanner(System.in);
        System.out.println("Nombre del archivo de salida:");
        String file = in.nextLine();
        if(file.equals("")){
            file = new SimpleDateFormat("yyyMMdd_HHmm").format(Calendar.getInstance().getTime());
        }
        app.setFile(file).run();
        
        /*final GUIScreen guiScreen = TerminalFacade.createGUIScreen();
        final Window window = new Window("Sample window");
        window.setWindowSizeOverride(new TerminalSize(130,50));
        window.setSoloWindow(true);
        
        Panel panelHolder = new Panel("Holder panel",Orientation.VERTICAL);
        
        Panel panel = new Panel("Panel with a right-aligned button");

        panel.setLayoutManager(new VerticalLayout());
        Button button = new Button("Button");
        button.setAlignment(Component.Alignment.RIGHT_CENTER);
        panel.addComponent(button, LinearLayout.GROWS_HORIZONTALLY);
        
        Table table = new Table(6);
        table.setColumnPaddingSize(5);
        
        Component[] row1 = new Component[6];
        row1[0] = new Label("Field-1----");
        row1[1] = new Label("Field-2");
        row1[2] = new Label("Field-3");
        row1[3] = new Label("Field-4");
        row1[4] = new Label("Field-5");
        row1[5] = new Label("Field-6");
        
        table.addRow(row1);
        panel.addComponent(table);
        
        panelHolder.addComponent(panel);

        window.addComponent(panelHolder);
        window.addComponent(new EmptySpace());
        
        final Window newWindow = new Window("This window should be of the same size as the previous one");

        Button quitButton = new Button("Show next window", new Action() {

            public void doAction() {
                
                newWindow.setWindowSizeOverride(new TerminalSize(130,50));
                newWindow.setSoloWindow(true);
                
                               
                
                Button exitBtn = new Button("Exit", new Action() {
                    
                    public void doAction() {
                        // TODO Auto-generated method stub
                        newWindow.close();
                        window.close();
                    }
                });
                
                exitBtn.setAlignment(Alignment.RIGHT_CENTER);
                
                newWindow.addComponent(exitBtn, LinearLayout.GROWS_HORIZONTALLY);
                
                guiScreen.showWindow(newWindow);
            }
        });
        quitButton.setAlignment(Component.Alignment.RIGHT_CENTER);
        window.addComponent(quitButton, LinearLayout.GROWS_HORIZONTALLY);

        guiScreen.getScreen().startScreen();
        guiScreen.showWindow(window);
        guiScreen.getScreen().stopScreen();
**/
        
    }
}

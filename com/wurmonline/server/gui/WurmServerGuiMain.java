// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.gui;

import javafx.scene.input.TouchPoint;
import javafx.event.EventTarget;
import javafx.event.Event;
import javafx.scene.input.PickResult;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import java.util.List;
import java.io.IOException;
import java.util.logging.Level;
import java.util.Collection;
import javafx.scene.image.Image;
import java.util.ArrayList;
import javafx.scene.input.TouchEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import com.wurmonline.server.Servers;
import java.util.Set;
import com.wurmonline.server.utils.SimpleArgumentParser;
import java.util.HashSet;
import java.util.logging.Logger;
import javafx.application.Application;

public final class WurmServerGuiMain extends Application
{
    private static final Logger logger;
    
    public static void main(final String[] args) {
        WurmServerGuiMain.logger.info("WurmServerGuiMain starting");
        final HashSet<String> allowedArgStrings = new HashSet<String>();
        for (final GuiCommandLineArgument argument : GuiCommandLineArgument.values()) {
            allowedArgStrings.add(argument.getArgumentString());
        }
        final SimpleArgumentParser parser = new SimpleArgumentParser(args, allowedArgStrings);
        String dbToStart = "";
        if (parser.hasOption(GuiCommandLineArgument.START.getArgumentString())) {
            dbToStart = parser.getOptionValue(GuiCommandLineArgument.START.getArgumentString());
            if (dbToStart == null || dbToStart.isEmpty()) {
                System.err.println("Start param needs to be followed by server dir: Start=<ServerDir>");
                System.exit(1);
            }
        }
        Servers.arguments = parser;
        String adminPass = "";
        if (parser.hasOption(GuiCommandLineArgument.ADMIN_PWD.getArgumentString())) {
            adminPass = parser.getOptionValue(GuiCommandLineArgument.ADMIN_PWD.getArgumentString());
            if (adminPass == null || adminPass.isEmpty()) {
                System.err.println("The admin password needs to be set or it will not be possible to change the settings within the game.");
            }
            else {
                WurmServerGuiController.adminPassword = adminPass;
            }
        }
        if (!dbToStart.isEmpty()) {
            System.out.println("Should start without GUI here!");
            WurmServerGuiController.startDB(dbToStart);
        }
        else {
            Application.launch((Class)WurmServerGuiMain.class, (String[])null);
        }
        WurmServerGuiMain.logger.info("WurmServerGuiMain finished");
    }
    
    public void start(final Stage primaryStage) {
        try {
            final FXMLLoader loader = new FXMLLoader(WurmServerGuiMain.class.getResource("WurmServerGui.fxml"));
            final TabPane page = (TabPane)loader.load();
            final Scene scene = new Scene((Parent)page);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Wurm Unlimited Server");
            primaryStage.addEventFilter(TouchEvent.ANY, event -> {
                event.consume();
                final TouchPoint touchPoint = event.getTouchPoint();
                final int clickCount = 1;
                final MouseEvent mouseEvent = new MouseEvent(event.getSource(), event.getTarget(), MouseEvent.MOUSE_CLICKED, touchPoint.getX(), touchPoint.getY(), touchPoint.getScreenX(), touchPoint.getScreenY(), MouseButton.PRIMARY, clickCount, false, false, false, false, true, false, false, true, false, false, (PickResult)null);
                final Scene yourScene = primaryStage.getScene();
                Event.fireEvent((EventTarget)yourScene.getRoot(), (Event)mouseEvent);
            });
            final List<Image> iconsList = new ArrayList<Image>();
            iconsList.add(new Image("com/wurmonline/server/gui/img/icon2_16.png"));
            iconsList.add(new Image("com/wurmonline/server/gui/img/icon2_32.png"));
            iconsList.add(new Image("com/wurmonline/server/gui/img/icon2_64.png"));
            iconsList.add(new Image("com/wurmonline/server/gui/img/icon2_128.png"));
            primaryStage.getIcons().addAll((Collection)iconsList);
            primaryStage.show();
            final WurmServerGuiController controller = (WurmServerGuiController)loader.getController();
            controller.setStage(primaryStage);
            scene.getWindow().setOnCloseRequest(ev -> {
                if (!controller.shutdown()) {
                    ev.consume();
                }
            });
        }
        catch (IOException ex) {
            WurmServerGuiMain.logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
    
    static {
        logger = Logger.getLogger(WurmServerGuiMain.class.getName());
    }
}

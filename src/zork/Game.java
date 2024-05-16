package zork;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Game {

  public static HashMap<String, Room> roomMap = new HashMap<String, Room>();
  public static HashMap<String, Item> itemMap = new HashMap<String, Item>();


  private Parser parser;
  private Room currentRoom;

  /**
   * Create the game and initialise its internal map.
   */
  public Game() {
    try {
      initGameInfo("src\\zork\\data\\gameinfo.json");
      initRooms("src\\zork\\data\\rooms.json");
      initItems("src\\zork\\data\\items.json");
      currentRoom = roomMap.get("Lobby");
    } catch (Exception e) {
      e.printStackTrace();
    }
    parser = new Parser();
  }

  private void initGameInfo(String fileName) throws Exception {
    Path path = Path.of(fileName);
    String jsonString = Files.readString(path);
    JSONParser parser = new JSONParser();
    JSONObject json = (JSONObject) parser.parse(jsonString);

    JSONObject jsonInfo = (JSONObject) json.get("gameinfo");
    JSONArray introMessage = (JSONArray) jsonInfo.get("intromessage");

    GameInfo.introMessage = new String[introMessage.size()];
    for (int i = 0; i < introMessage.size(); i++) {
      GameInfo.introMessage[i] = (String)introMessage.get(i);
    }
  }

  private void initItems(String fileName) throws Exception {
    Path path = Path.of(fileName);
    String jsonString = Files.readString(path);
    JSONParser parser = new JSONParser();
    JSONObject json = (JSONObject) parser.parse(jsonString);
  
    JSONArray jsonItems = (JSONArray) json.get("items");

    
    for (Object itemObj : jsonItems) {
      
      String itemName = (String) ((JSONObject) itemObj).get("name");
      String itemId = (String) ((JSONObject) itemObj).get("id");
      String description = (String) ((JSONObject) itemObj).get("description");
      int itemWeight = Integer.parseInt((String) ((JSONObject) itemObj).get("weight"));
      boolean isOpenable = Boolean.parseBoolean((String) ((JSONObject) itemObj).get("isOpenable"));

      String loc_id = (String) ((JSONObject) itemObj).get("room_id");
      Item item = new Item(itemWeight, itemName, isOpenable, description);

      if (loc_id != null){
        roomMap.get(loc_id).addItem(item);
      }else{
        loc_id = (String) ((JSONObject) itemObj).get("item_id");
        itemMap.get(loc_id).addItem(item);
      }

      itemMap.put(itemId, item);
    
    }
  }

  private void initRooms(String fileName) throws Exception {
    Path path = Path.of(fileName);
    String jsonString = Files.readString(path);
    JSONParser parser = new JSONParser();
    JSONObject json = (JSONObject) parser.parse(jsonString);

    JSONArray jsonRooms = (JSONArray) json.get("rooms");

    for (Object roomObj : jsonRooms) {
      Room room = new Room();
      String roomName = (String) ((JSONObject) roomObj).get("name");
      String roomId = (String) ((JSONObject) roomObj).get("id");
      String roomShortDescription = (String) ((JSONObject) roomObj).get("shortdescription");
      String roomLongDescription = (String) ((JSONObject) roomObj).get("longdescription");

      room.setLongDescription(roomLongDescription);
      room.setShortDescription(roomShortDescription);
      room.setRoomName(roomName);

      JSONArray jsonExits = (JSONArray) ((JSONObject) roomObj).get("exits");
      ArrayList<Exit> exits = new ArrayList<Exit>();
      for (Object exitObj : jsonExits) {
        String direction = (String) ((JSONObject) exitObj).get("direction");
        String adjacentRoom = (String) ((JSONObject) exitObj).get("adjacentRoom");
        String keyId = (String) ((JSONObject) exitObj).get("keyId");
        Boolean isLocked = (Boolean) ((JSONObject) exitObj).get("isLocked");
        Boolean isOpen = (Boolean) ((JSONObject) exitObj).get("isOpen");
        Exit exit = new Exit(direction, adjacentRoom, isLocked, keyId, isOpen);
        exits.add(exit);
      }
      room.setExits(exits);
      roomMap.put(roomId, room);
    }
  }

  /**
   * Main play routine. Loops until end of play.
   */
  public void play() {
    printWelcome();

    boolean finished = false;
    while (!finished) {
      Command command;
      try {
        command = parser.getCommand();
        finished = processCommand(command);
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
    System.out.println("Thank you for playing.  Good bye.");
  }

  /**
   * Print out the opening message for the player.
   */
  private void printWelcome() {
    GameInfo.clearScreen();
    for (String s : GameInfo.introMessage) {
      System.out.println(s);
      System.out.println();
    }
    System.out.println();
    System.out.println("Type 'help' if you need help.");
    System.out.println();
    System.out.println(currentRoom.longDescription());
  }

  /**
   * Given a command, process (that is: execute) the command. If this command ends
   * the game, true is returned, otherwise false is returned.
   */
  private boolean processCommand(Command command) {
    if (command.isUnknown()) {
      System.out.println("I don't know what you mean...");
      return false;
    }

    String commandWord = command.getCommandWord();
    if (commandWord.equals("help"))
      printHelp();
    else if (commandWord.equals("go"))
      goRoom(command);
    else if (commandWord.equals("quit")) {
      if (command.hasSecondWord())
        System.out.println("Quit what?");
      else
        return true; // signal that we want to quit
    } else if (commandWord.equals("eat")) {
      System.out.println("Do you really think you should be eating at a time like this?");
    }
    return false;
  }

  // implementations of user commands:

  /**
   * Print out some help information. Here we print some stupid, cryptic message
   * and a list of the command words.
   */
  private void printHelp() {
    System.out.println("You are lost. You are alone. You wander");
    System.out.println("around at Monash Uni, Peninsula Campus.");
    System.out.println();
    System.out.println("Your command words are:");
    parser.showCommands();
  }

  /**
   * Try to go to one direction. If there is an exit, enter the new room,
   * otherwise print an error message.
   */
  private void goRoom(Command command) {
    if (!command.hasSecondWord()) {
      // if there is no second word, we don't know where to go...
      System.out.println("Go where?");
      return;
    }

    String direction = command.getSecondWord();

    // Try to leave current room.
    Room nextRoom = currentRoom.nextRoom(direction);

    if (nextRoom == null)
      System.out.println("There is no door!");
    else {
      currentRoom = nextRoom;
      System.out.println(currentRoom.longDescription() + "\n\n");
    }
  }
}

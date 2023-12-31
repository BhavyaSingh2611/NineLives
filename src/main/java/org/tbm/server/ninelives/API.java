package org.tbm.server.ninelives;

import static spark.Spark.get;

import org.json.simple.JSONValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
public class API {
    public static void main(String[] args) {
        get("/team", (req, res) -> getTeam());
        get("/player", (req, res) -> getPlayer());
        get("/playerteams", (req, res) -> "{ \"Sakura Syndicate\": [\"TheBayker\", \"Elainettie\", \"SocketKat\"], \"The Greybeards\": [\"Danbearpig82\", \"PyfroAUS\", \"Sir_Laughalot\"],    \"GingerBread Men\": [\"FabledDayes\", \"GaymerSalsa\", \"TheWickedWeasel\"],    \"Creeper Toes\": [\"Blundingen\", \"Noesshie\", \"Tadpole_Milk_\"],    \"The Magenta Menaces\": [\"Kayoss_Taz\", \"MommaMelissa14\", \"Corrupted_Tree\"],    \"Mist Wraiths\": [\"TheXpNetwork\", \"OfftheRailsIR\", \"Givized13\"],    \"Solar Squadron\": [\"Manky_Hamster\", \"Mefallit\", \"Chipz_N_Dipz\"],    \"Paint it Black\": [\"RSNFreud\", \"SiegeTheDay\", \"BlackClad1derer\"],    \"Busta Limes\": [\"GagetGaming\", \"Mistyjoy11\", \"Multibobob\"],    \"O-RNG-EE CRUSH\": [\"MCTCReese\", \"AggroNinja89\", \"NarcoleptiCheeta\"],    \"The 9ice Crew\": [\"Magganna\", \"Sorgan_D\", \"CelticDragoness\"],    \"Cyanide Squad\": [\"Sling_n_Stone\", \"Shortzelda22\", \"ShallowStack\"],    \"Queso Blanco\": [\"GreatLakesGirl\", \"DragonNebula42\", \"Theperfectshot3\"],    \"The Smurfs\": [\"ZeeBeeBlue\", \"Clutchypoo\", \"SonOfAlister\"],    \"Purple Pirates\": [\"oISketcHIo\", \"Linahun\", \"VannahVan16\"]}");
    }

    public static String getTeam() {
        if (HologramManager.teamPlacements.isEmpty()) return "{}";
        String[] teams = HologramManager.teamPlacements.split(";");
        Map<String, Map<String, Integer>> hash = new HashMap<>();
        for (String s : teams) {
            String[] team = s.split(">");
            String teamName = team[1];
            String teamScore = team[0];

            if (Objects.equals(teamName, "12345678")) continue;

            int teamLives = 0;
            String[] teamLivesList = HologramManager.teamLivesPlacements.split(";");
            for (String teamLive : teamLivesList) {
                String[] temp = teamLive.split(">");
                if (temp[1].equals(teamName)) {
                    teamLives = Integer.parseInt(temp[0]);
                    break;
                }
            }
            Map<String, Integer> info = new HashMap<>();
            info.put("score", Integer.parseInt(teamScore));
            info.put("lives", teamLives);

            hash.put(teamName, info);
        }

        return JSONValue.toJSONString(hash);
    }

    public static String getPlayer() {
        if (HologramManager.playerPlacements.isEmpty()) return "{}";
        String[] players = HologramManager.playerPlacements.split(";");
        Map<String, Map<String, Integer>> hash = new HashMap<>();
        for (String s : players) {
            String[] player = s.split(">");
            String playerName = player[1];
            String playerScore = player[0];

            if (Objects.equals(playerName, "12345678")) continue;

            int playerDeaths = 0;
            String[] deathPlayers = HologramManager.deathPlacements.split(";");
            for (String deathPlayer : deathPlayers) {
                String[] temp = deathPlayer.split(">");
                if (temp[1].equals(playerName)) {
                    playerDeaths = Integer.parseInt(temp[0]);
                    break;
                }
            }
            Map<String, Integer> info = new HashMap<>();
            info.put("score", Integer.parseInt(playerScore));
            info.put("deaths", playerDeaths);

            hash.put(playerName, info);
        }

        return JSONValue.toJSONString(hash);
    }
}

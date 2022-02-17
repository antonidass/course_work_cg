import java.util.ArrayList;

public class Light {
    private static int numbLights = 0;
    private static ArrayList<Vector> lightSource = new ArrayList<>();


    public static void addLightSource(Vector source) {
        lightSource.add(source);
        numbLights++;
    }

    public static ArrayList<Vector> getLightSource() {
        return lightSource;
    }

    public static void removeLightSource(Vector source) {
        for (int i = 0; i < lightSource.size(); i++) {
            if (lightSource.get(i).equals(source)) {
                lightSource.remove(i);
                break;
            }
        }
        numbLights--;
    }

    public static int getNumbLights() {
        return numbLights;
    }

    public static void setNumbLights(int numbLights) {
        Light.numbLights = numbLights;
    }
}



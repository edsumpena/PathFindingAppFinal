package App.Converters;

import App.MainActivity;

import java.util.ArrayList;

public class LineArrayProcessor {
    public static String getCurrentSetting(int setting) {
        String mode = "null";
        switch (setting) {
            case 0:
                mode = "line";
                break;
            case 1:
                mode = "curve";
                break;
            case 2:
                mode = "reverse";
                break;
            case 3:
                mode = "strafe";
                break;
            case 4:
                mode = "spline";
                break;

        }
        return mode;
    }

    public static ArrayList<ArrayList<Integer>> polyLineList(ArrayList<ArrayList<String>> optionsAndCoords) {
        int i = 0;
        int x = 0;
        ArrayList<ArrayList<Integer>> output = new ArrayList<>();
        ArrayList<Integer> adder = new ArrayList<>();
        if (!optionsAndCoords.isEmpty()) {
            if (optionsAndCoords.get(0).size() >= 1) {
                adder.add(Integer.valueOf(optionsAndCoords.get(1).get(0)));
                adder.add(Integer.valueOf(optionsAndCoords.get(1).get(1)));
                while (optionsAndCoords.get(0).size() > i) {
                    if (optionsAndCoords.get(0).get(i).equals("curve")) {
                        output.add(adder);
                        adder.clear();
                    } else {
                        try {
                            adder.add(Integer.valueOf(optionsAndCoords.get(3).get(x)));
                            adder.add(Integer.valueOf(optionsAndCoords.get(3).get(x + 1)));
                        } catch (Exception e){
                            e.printStackTrace();
                            break;
                        }
                    }
                    x += 2;
                    i += 1;
                }
                output.add(adder);
                return output;
            } else {
                adder.add(-1);
                adder.add(-1);
                adder.add(-1);
                output.add(adder);
                return output;
            }
        } else {
            adder.add(-2);
            adder.add(-2);
            adder.add(-2);
            output.add(adder);
            return output;
        }
    }

    public static int[] extractX(ArrayList<Integer> circles, int valsPerCircleX) {
        ArrayList<Integer> xVals = new ArrayList<>();
        int i = 0;
        while (circles.size() > i) {
            try {
                xVals.add(circles.get(i) + MainActivity.xOffset);
            } catch (Exception e) {
            }
            i += valsPerCircleX;
        }

        int[] xValToArray = new int[xVals.size()];
        i = 0;
        while (xVals.size() > i) {
            xValToArray[i] = xVals.get(i);
            i += 1;
        }
        return xValToArray;
    }

    public static int[] extractY(ArrayList<Integer> circles, int valsPerCircleY) {
        ArrayList<Integer> yVals = new ArrayList<>();
        int i = 0;
        while (circles.size() > i) {
            try {
                yVals.add(circles.get(i + 1) + MainActivity.yOffset);
            } catch (Exception e) {
            }
            i += valsPerCircleY;
        }

        int[] yValToArray = new int[yVals.size()];
        i = 0;
        while (yVals.size() > i) {
            yValToArray[i] = yVals.get(i);
            i += 1;
        }
        return yValToArray;
    }
}

package App.Converters;

import com.acmerobotics.roadrunner.Pose2d;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class FromAndToPose2D {
    //424 field size
    static double CANVAS_SIZE = 0.0;

//---------------------------------------------------------------------------------------------------------

    public static ArrayList<Pose2d> pointsToPose2d(ArrayList<Integer> points, int firstXIndex,
                                                   int firstYIndex, int valsPerCircle){  //feed circles array into here
        ArrayList<Pose2d> pose2ds = new ArrayList<>();
        int i = 0;
        while(points.size() > i){
            try {
                pose2ds.add(canvasToFieldSpace(Double.valueOf(points.get(i + firstXIndex)), Double.valueOf(points.get(i + firstYIndex))));
            } catch (Exception e){
                e.printStackTrace();
                break;
            }
            i += valsPerCircle;
        }
        return pose2ds;
    }
    public static ArrayList<Integer> pose2dToPoints(ArrayList<Pose2d> poses, ArrayList<Integer> colors){
        ArrayList<Integer> points = new ArrayList<>();
        int i = 0;
        while(poses.size() > i){
            points.add((int) fieldToCanvasSpace(poses.get(i))[0]);
            points.add((int) fieldToCanvasSpace(poses.get(i))[1]);
            points.add(colors.get(i));
            i += 3;
        }
        return points;
    }

//---------------------------------------------------------------------------------------------------------

    public static ArrayList<Integer> getColors(ArrayList<Integer> points, int firstColorIndex, int valsPerCircle){
        ArrayList<Integer> colors = new ArrayList<>();
        colors = null;
        int i = 0;
        while(points.size() > i){
            try {
                colors.add(points.get(i + firstColorIndex));
            } catch (Exception e){
                e.printStackTrace();
                break;
            }
            i += valsPerCircle;
        }
        return colors;
    }

//---------------------------------------------------------------------------------------------------------

    public static Pose2d canvasToFieldSpace(double vectorX, double vectorY) {
        double fieldY = -(vectorX - CANVAS_SIZE / 2.0) * (255.0 / CANVAS_SIZE);  //255.0 or 144.0?
        double fieldX = -(vectorY - CANVAS_SIZE / 2.0) * (255.0 / CANVAS_SIZE);
        return new Pose2d(fieldX, fieldY, 0);
    }
    public static double[] fieldToCanvasSpace(Pose2d pose) {
        double canvasY = -(pose.getX() * (CANVAS_SIZE / 255.0)) + CANVAS_SIZE / 2.0;
        double canvasX = -(pose.getY() * (CANVAS_SIZE / 255.0)) + CANVAS_SIZE / 2.0;
        double[] returnVals = {canvasX,canvasY};
        return returnVals;
    }
    public static void setCanvasSize(double size){
        CANVAS_SIZE = size;
    }
}

package App.Wrappers;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;

import java.util.ArrayList;

public class TrajBuilderWrapper {
    public static TrajectoryBuilder getWheelTrajectoryBuilder(ArrayList<Pose2d> pose2ds, ArrayList<String> options) {
        TrajectoryBuilder trajbuider = new TrajectoryBuilder(pose2ds.get(0), DriverConstraintsWrapper.getDriveConstraints(), 2500);
        int i = 0;
        while(options.size() > i){
            if(options.get(i).equalsIgnoreCase("line"))
                trajbuider.lineTo(pose2ds.get(i + 1).pos());
            else if(options.get(i).equalsIgnoreCase("curve"))
                trajbuider.turnTo(pose2ds.get(i + 1).getHeading());
            else if(options.get(i).equalsIgnoreCase("strafe"))
                trajbuider.strafeTo(pose2ds.get(i + 1).pos());
            else if(options.get(i).equalsIgnoreCase("reverse"))
                trajbuider.reverse();
            else if(options.get(i).equalsIgnoreCase("spline"))
                trajbuider.splineTo(pose2ds.get(i + 1));
            else
            i += 1;
        }
        return trajbuider;
    }

    public static ArrayList<String> getArmTrajectory(ArrayList<String> options, ArrayList<Integer> location){
        ArrayList<String> moveArm = new ArrayList<>();
        int i = 0;
        while(moveArm.size() > i){
            moveArm.add(options.get(i));
            moveArm.add(String.valueOf(location.get(i)));
        }
        return moveArm;
    }

    public static ArrayList<String> getServoTrajectory(ArrayList<String> options, ArrayList<Integer> location){
        ArrayList<String> moveServo = new ArrayList<>();
        int i = 0;
        while(moveServo.size() > i){
            moveServo.add(options.get(i));
            moveServo.add(String.valueOf(location.get(i)));
        }
        return moveServo;
    }
}

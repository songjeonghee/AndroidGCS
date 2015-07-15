package org.droidplanner.core.mission.waypoints;

import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;

import org.droidplanner.core.helpers.coordinates.Coord2D;
import org.droidplanner.core.helpers.coordinates.Coord3D;
import org.droidplanner.core.helpers.geoTools.GeoTools;
import org.droidplanner.core.mission.Mission;
import org.droidplanner.core.mission.MissionItem;
import org.droidplanner.core.mission.MissionItemType;
import org.droidplanner.core.mission.survey.SurveyImpl;
import org.droidplanner.core.polygon.Polygon;
import org.droidplanner.core.survey.CameraInfo;
import org.droidplanner.core.survey.SurveyData;
import org.droidplanner.core.survey.grid.GridBuilder;

import java.util.ArrayList;
import java.util.List;

public class StructureScanner extends SpatialCoordItem {
    private double radius = (10.0);
    private double heightStep = (5);
    private int numberOfSteps = 2;
    private boolean crossHatch = false;
    SurveyData survey = new SurveyData();

    public StructureScanner(Mission mission, Coord3D coord) {
        super(mission, coord);
    }

    public StructureScanner(MissionItem item) {
        super(item);
    }

    @Override
    public List<msg_mission_item> packMissionItem() {
        List<msg_mission_item> list = new ArrayList<msg_mission_item>();
        packROI(list);
        packCircles(list);
        if (crossHatch) {
            packHatch(list);
        }
        return list;
    }

    private void packROI(List<msg_mission_item> list) {
        RegionOfInterest roi = new RegionOfInterest(mission, new Coord3D(
                coordinate, (0.0)));
        list.addAll(roi.packMissionItem());
    }

    private void packCircles(List<msg_mission_item> list) {
        for (double altitude = coordinate.getAltitude(); altitude <= getTopHeight(); altitude += heightStep) {
            Circle circle = new Circle(mission, new Coord3D(coordinate, (altitude)));
            circle.setRadius(radius);
            list.addAll(circle.packMissionItem());
        }
    }

    private void packHatch(List<msg_mission_item> list) {
        Polygon polygon = new Polygon();
        for (double angle = 0; angle <= 360; angle += 10) {
            polygon.addPoint(GeoTools.newCoordFromBearingAndDistance(coordinate,
                    angle, radius));
        }

        Coord2D corner = GeoTools.newCoordFromBearingAndDistance(coordinate,
                -45, radius * 2);


        survey.setAltitude(getTopHeight());

        try {
            survey.update(0.0, survey.getAltitude(), survey.getOverlap(), survey.getSidelap());
            GridBuilder grid = new GridBuilder(polygon, survey, corner);
            for (Coord2D point : grid.generate(false).gridPoints) {
                list.add(SurveyImpl.packSurveyPoint(point, getTopHeight()));
            }

            survey.update(90.0, survey.getAltitude(), survey.getOverlap(), survey.getSidelap());
            GridBuilder grid2 = new GridBuilder(polygon, survey, corner);
            for (Coord2D point : grid2.generate(false).gridPoints) {
                list.add(SurveyImpl.packSurveyPoint(point, getTopHeight()));
            }
        } catch (Exception e) { // Should never fail, since it has good polygons
        }

    }

    public List<Coord2D> getPath() {
        List<Coord2D> path = new ArrayList<Coord2D>();
        for (msg_mission_item msg_mission_item : packMissionItem()) {
            if (msg_mission_item.command == MAV_CMD.MAV_CMD_NAV_WAYPOINT) {
                path.add(new Coord2D(msg_mission_item.x, msg_mission_item.y));
            }
            if (msg_mission_item.command == MAV_CMD.MAV_CMD_NAV_LOITER_TURNS) {
                for (double angle = 0; angle <= 360; angle += 12) {
                    path.add(GeoTools.newCoordFromBearingAndDistance(coordinate, angle, radius));
                }
            }

        }
        return path;

    }

    @Override
    public void unpackMAVMessage(msg_mission_item mavMsg) {
    }

    @Override
    public MissionItemType getType() {
        return MissionItemType.CYLINDRICAL_SURVEY;
    }


    private double getTopHeight() {
        return (coordinate.getAltitude() + (numberOfSteps - 1) * heightStep);
    }

    public double getEndAltitude() {
        return heightStep;
    }

    public int getNumberOfSteps() {
        return numberOfSteps;
    }

    public double getRadius() {
        return radius;
    }

    public Coord2D getCenter() {
        return coordinate;
    }

    public void setRadius(int newValue) {
        radius = (newValue);
    }

    public void enableCrossHatch(boolean isEnabled) {
        crossHatch = isEnabled;
    }

    public boolean isCrossHatchEnabled() {
        return crossHatch;
    }

    public void setAltitudeStep(int newValue) {
        heightStep = (newValue);
    }

    public void setNumberOfSteps(int newValue) {
        numberOfSteps = newValue;
    }

    public void setCamera(CameraInfo cameraInfo) {
        survey.setCameraInfo(cameraInfo);
    }

    public String getCamera() {
        return survey.getCameraName();
    }

    public SurveyData getSurveyData() {
        return survey;
    }

}

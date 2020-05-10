package app.models;

import app.models.maps.Coordinate;
import app.models.maps.Line;
import app.models.maps.Stop;
import app.models.maps.Street;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public abstract class TripSimulation {
    /**
     * Compute current position of vehicle.
     * @param currentTime
     * @param startTimePos
     * @param endTimePos
     * @param startStop
     * @param endStop
     * @param line
     * @return Coord of a point where the actual bus is
     */
    public static Coordinate dotPosition(LocalTime currentTime, LocalTime startTimePos, LocalTime endTimePos, Stop startStop, Stop endStop, Line line) {


        Coordinate finalCoord = null;
        try {
            finalCoord = (Coordinate) startStop.getCoordinate().clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        Coordinate follow;
        List<Street> Streets = new ArrayList<>();

        LocalTime tripTimeTotal = endTimePos.minusHours(startTimePos.getHour())
                .minusMinutes(startTimePos.getMinute())
                .minusSeconds(startTimePos.getSecond());

        LocalTime tripTimeActual = currentTime.minusHours(startTimePos.getHour())
                .minusMinutes(startTimePos.getMinute())
                .minusSeconds(startTimePos.getSecond());

        int actualSeconds = (tripTimeActual.getHour() * 60 * 60) + (tripTimeActual.getMinute() * 60) + (tripTimeActual.getSecond());
        int totalSeconds = (tripTimeTotal.getHour() * 60 * 60) + (tripTimeTotal.getMinute() * 60) + (tripTimeTotal.getSecond());

        float actualPercent = (actualSeconds * 100.0f) / totalSeconds;

        double lineLenght = line.getStopsLength(startStop, endStop);

        double lenghtPassed = (actualPercent / 100.0) * lineLenght;

        Streets = line.getStreetsBetween(startStop, endStop);


        for (int i = 0; i < Streets.size(); i++) {
            if (lenghtPassed == 0) {
                break;
            }
            if (i == Streets.size()-1){
                follow = endStop.getCoordinate(); // bod konca ulice na ktorej sa bus nachádza
            }
            else
            {
                follow = line.followPoint(Streets.get(i), Streets.get(i + 1)); // bod konca ulice na ktorej sa bus nachádza
            }

            if (line.changeX(Streets.get(i))) { // kontrola či sa hýbeme po X ose
                if ((Math.abs(follow.getX() - finalCoord.getX())) <= lenghtPassed) { //kontrola či sa bod nachádza na aktuálnej ulici
                    lenghtPassed -= ((Math.abs(follow.getX() - finalCoord.getX())));
                    if (line.plusX(finalCoord, follow)) { //kontrola smeru po X ose
                        finalCoord.setX(finalCoord.getX() + (Math.abs(follow.getX() - finalCoord.getX())));
                    } else {
                        finalCoord.setX(finalCoord.getX() - (Math.abs(follow.getX() - finalCoord.getX())));
                    }
                    lenghtPassed -= ((Math.abs(follow.getX() - finalCoord.getX())));
                } else {
                    if (line.plusX(finalCoord, follow)) { //kontrola smeru po X ose
                        finalCoord.setX(finalCoord.getX() + lenghtPassed);
                    } else {
                        finalCoord.setX(finalCoord.getX() - lenghtPassed);
                    }
                    lenghtPassed = 0;
                }
            } else {
                if ((Math.abs(follow.getY() - finalCoord.getY())) <= lenghtPassed) { //kontrola či sa bod nachádza na aktuálnej ulici
                    lenghtPassed -= ((Math.abs(follow.getY() - finalCoord.getY())));
                    if (line.plusY(finalCoord, follow)) { //kontrola smeru po Y ose
                        finalCoord.setY(finalCoord.getY() + (Math.abs(follow.getY() - finalCoord.getY())));
                    } else {
                        finalCoord.setY(finalCoord.getY() - (Math.abs(follow.getY() - finalCoord.getY())));
                    }
                    lenghtPassed -= ((Math.abs(follow.getY() - finalCoord.getY())));
                } else {
                    if (line.plusY(finalCoord, follow)) { //kontrola smeru po Y ose
                        finalCoord.setY(finalCoord.getY() + lenghtPassed);
                    } else {
                        finalCoord.setY(finalCoord.getY() - lenghtPassed);
                    }
                    lenghtPassed = 0;
                }
            }
        }
        return finalCoord;

    }
}

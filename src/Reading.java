import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

public class Reading {
    public Reading(Float temp, Float press, Float hum, Float co, Float no2, Float so2, Float time) {
        this.temp = temp;
        this.press = press;
        this.hum = hum;
        this.co = co;
        this.no2 = no2;
        this.so2 = so2;
        this.time = time;
    }

    //Temperature,Pressure,Humidity,CO,NO2,SO2,
    private Float temp;//0
    private Float press;
    private Float hum;
    private Float co;
    private Float no2;
    private Float so2;
    private Float time;
    //vector and scalar timestamp¿? for sorting


    public Float getTime() {
        return time;
    }

    public void setTime(Float time) {
        this.time = time;
    }

    public Reading() {
    }

    public Float getTemp() {
        return temp;
    }

    public void setTemp(Float temp) {
        this.temp = temp;
    }

    public Float getPress() {
        return press;
    }

    public void setPress(Float press) {
        this.press = press;
    }

    public Float getHum() {
        return hum;
    }

    public void setHum(Float hum) {
        this.hum = hum;
    }

    public Float getCo() {
        return co;
    }

    public void setCo(Float co) {
        this.co = co;
    }

    public Float getNo2() {
        return no2;
    }

    public void setNo2(Float no2) {
        this.no2 = no2;
    }

    public Float getSo2() {
        return so2;
    }

    public void setSo2(Float so2) {
        this.so2 = so2;
    }

    public float[] generateReadings(long currentTime) {
        //currentTime = System.nanoTime() - start
        //start = int star_t = (int) System.nanoTime(); At beggining of life of node

        String[] t_val = new String[6];
        try {
            t_val = Files.readAllLines(Paths.get("src/readings.csv")).get((int) Math.abs(currentTime % 101)).split(",");//Copiado en proyecto bien?¡¿
        } catch (InvalidPathException | IOException e) {
            System.out.println("Error path");
        }
        int sizeT_val = t_val.length;
        t_val = Arrays.copyOf(t_val, 6);
        Arrays.fill(t_val, sizeT_val, 6, "");
        for (int x = 0; x <= 5; x++) {
            if (t_val[x].isEmpty())
                t_val[x] = String.valueOf(-1);
        }
        setTemp(Float.parseFloat(t_val[0]));
        setPress(Float.parseFloat(t_val[1]));
        setHum(Float.parseFloat(t_val[2]));
        setCo(Float.parseFloat(t_val[3]));
        setNo2(Float.parseFloat(t_val[4]));
        setSo2(Float.parseFloat(t_val[5]));
        setTime((float)currentTime);

        return new float[]{getTemp(), getPress(), getHum(), getCo(), getNo2(), getSo2(),getTime()};


    }


    public boolean equals(Reading o) {

        if (o == null || getClass() != o.getClass()) return false;
        return time.equals(o.time) &&
                temp.equals(o.temp) &&
                press.equals(o.press) &&
                hum.equals(o.hum) &&
                co.equals(o.co) &&
                no2.equals(o.no2) &&
                so2.equals(o.so2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(temp, press, hum, co, no2, so2, time);
    }
}
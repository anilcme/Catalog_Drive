import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.json.*;
import java.math.*;


class Point {
    int x;
    BigInteger y;

    Point(int x, BigInteger y) {
        this.x = x;
        this.y = y;
    }
}

public class catalog {

   
    public static Map<String, Object> parseInput(String filePath) {
        List<Point> points = new ArrayList<>();
        int k = 0;

        try {
           
            String rawData = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonData = new JSONObject(rawData);

            int n = jsonData.getJSONObject("keys").getInt("n");
            k = jsonData.getJSONObject("keys").getInt("k");

            
            for (int i = 1; i <= n; i++) {
                if (jsonData.has(String.valueOf(i))) {
                    JSONObject pointData = jsonData.getJSONObject(String.valueOf(i));
                    if (pointData.has("base") && pointData.has("value")) {
                        int base = pointData.getInt("base");
                        String valueStr = pointData.getString("value");
                        BigInteger y = new BigInteger(valueStr, base); 
                        points.add(new Point(i, y)); 
                    } else {
                        System.err.println("Error parsing data for index " + i + ". Missing base or value property.");
                    }
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("points", points);
        result.put("k", k);

        return result;
    }

    
    public static BigDecimal lagrangeInterpolation(List<Point> points, int k) {
        BigDecimal constantTerm = BigDecimal.ZERO;

        for (int i = 0; i < k; i++) {
            int xi = points.get(i).x;
            BigInteger yi = points.get(i).y;
            BigDecimal li = BigDecimal.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    int xj = points.get(j).x;
                  
                    BigDecimal numerator = new BigDecimal(-xj);
                    BigDecimal denominator = new BigDecimal(xi - xj);
                    li = li.multiply(numerator.divide(denominator, MathContext.DECIMAL128));
                }
            }
          
            constantTerm = constantTerm.add(new BigDecimal(yi).multiply(li));
        }

        return constantTerm;
    }

    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Please provide the input file path as an argument.");
            return;
        }

        
        Map<String, Object> parsedData = parseInput(args[0]);
        List<Point> points = (List<Point>) parsedData.get("points");
        int k = (int) parsedData.get("k");

       
        BigDecimal constantTerm = lagrangeInterpolation(points, k);
        System.out.println("The constant term (c) is: " + constantTerm);
    }
}
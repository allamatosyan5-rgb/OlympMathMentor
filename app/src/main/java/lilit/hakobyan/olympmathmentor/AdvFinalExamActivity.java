package lilit.hakobyan.olympmathmentor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class AdvFinalExamActivity extends AppCompatActivity {

    private TextView tvProgress, tvTimer, tvQuestion, tvFeedback;
    private RadioGroup rgOptions;
    private RadioButton rbOption1, rbOption2, rbOption3, rbOption4;
    private Button btnAction;
    private ProgressBar progressBar;

    private int currentQuestionIndex = 0;
    private int score = 0;
    private boolean isAnswerSubmitted = false;

    private CountDownTimer countDownTimer;
    private final long EXAM_TIME_IN_MILLIS = 7200000; // 120 րոպե

    // 100 HARD OLYMPIAD-LEVEL QUESTIONS
    private String[] questions = {
            // Number Theory (1-20)
            "1. Find the number of trailing zeros in 1000!.",
            "2. What are the last two digits of 7^2024?",
            "3. Find the remainder when 2^2023 is divided by 7.",
            "4. How many positive divisors does 3600 have?",
            "5. Find the greatest common divisor (GCD) of 2^100 - 1 and 2^120 - 1.",
            "6. What is the value of Euler's totient function φ(100)?",
            "7. Solve for x: 5x ≡ 1 (mod 11).",
            "8. Find the highest power of 3 that divides 100!.",
            "9. How many pairs of integers (x, y) satisfy x^2 - y^2 = 2022?",
            "10. Find the remainder of 1! + 2! + 3! + ... + 100! when divided by 15.",
            "11. If p and q are primes such that p+q=31, what is pq?",
            "12. Which of the following is a Fermat prime?",
            "13. Find the last digit of 3^1000.",
            "14. How many zeros are at the end of the base-2 representation of 100!? (Highest power of 2 in 100!)",
            "15. Determine the sum of all positive divisors of 100.",
            "16. What is the smallest positive integer with exactly 10 divisors?",
            "17. Evaluate 1000^2 ≡ x (mod 13). Find x where 0 <= x < 13.",
            "18. Find the remainder when 10^10 + 10^100 + 10^1000 is divided by 7.",
            "19. Which number is a perfect number?",
            "20. If x^2 ≡ 1 (mod 8), how many solutions are there modulo 8?",
            // Algebra (21-40)
            "21. Find the sum of the roots of x^3 - 4x^2 + x + 6 = 0.",
            "22. If x + 1/x = 3, what is the value of x^3 + 1/x^3?",
            "23. Minimum value of x^2 + y^2 given x + y = 10.",
            "24. Find the product of the roots of 2x^4 - 3x^3 + x - 5 = 0.",
            "25. Simplify (1 + i)^10 where i^2 = -1.",
            "26. What is the maximum value of f(x) = -2x^2 + 12x - 5?",
            "27. If log_2(x) + log_2(x-3) = 2, find x.",
            "28. Evaluate the infinite sum: 1/2 + 1/4 + 1/8 + ...",
            "29. Find the coefficient of x^3 in the expansion of (2x - 1)^5.",
            "30. If a, b, c are positive and abc=1, find the minimum of a+b+c.",
            "31. Let f(x) = (x-1)/(x+1). Find f(f(f(x))).",
            "32. Solve the inequality |2x - 5| < 7.",
            "33. Evaluate det([1, 2; 3, 4]).",
            "34. Find the sum of the first 100 terms of an arithmetic progression where a1=2, d=3.",
            "35. What is the value of the golden ratio φ?",
            "36. The sum of an infinite geometric series is 15 and the first term is 3. Find the common ratio r.",
            "37. Find the value of x if 8^x = 32.",
            "38. Calculate the determinant of a 3x3 identity matrix.",
            "39. If x, y > 0 and xy = 16, what is the minimum value of x + 4y?",
            "40. Let P(x) be a polynomial such that P(x) leaves a remainder of 2 when divided by x-1. What is P(1)?",
            // Combinatorics (41-60)
            "41. How many ways can 5 distinct books be arranged on a shelf?",
            "42. How many ways to choose a committee of 3 from 10 people?",
            "43. How many anagrams can be made from the word 'MISSISSIPPI'?",
            "44. How many ways can 8 identical candies be distributed to 3 children?",
            "45. A coin is tossed 10 times. What is the probability of exactly 5 heads?",
            "46. How many diagonals are in a regular decagon (10-sided)?",
            "47. What is the coefficient of x^2 y^3 in (x+y)^5?",
            "48. In a room of 10 people, everyone shakes hands with everyone else. How many handshakes?",
            "49. From a standard 52-card deck, how many 5-card hands contain exactly 4 aces?",
            "50. How many paths are there from (0,0) to (5,5) moving only right or up?",
            "51. Determine the number of subsets of a set with 8 elements.",
            "52. If 5 people sit around a circular table, how many distinct seating arrangements exist?",
            "53. How many 4-digit numbers have all distinct digits (no leading zero)?",
            "54. In how many ways can you place 8 non-attacking rooks on an 8x8 chessboard?",
            "55. What is the probability of rolling a sum of 7 with two standard 6-sided dice?",
            "56. How many regions is a plane divided into by 5 intersecting lines (no 3 concurrent, no 2 parallel)?",
            "57. Using digits 1,2,3,4,5, how many odd 3-digit numbers can be formed (with replacement)?",
            "58. Find the expected number of heads when flipping 4 fair coins.",
            "59. How many derangements (permutations where no element is in its original place) exist for 4 items?",
            "60. A box has 3 red and 4 blue balls. Probability of drawing 2 red balls without replacement?",
            // Geometry (61-80)
            "61. In triangle ABC, a=3, b=4, c=5. What is the radius of the inscribed circle (inradius)?",
            "62. What is the area of an equilateral triangle with side length 6?",
            "63. A circle is inscribed in a square of side 8. What is the area of the circle?",
            "64. The sum of the interior angles of a convex 12-gon is:",
            "65. Find the length of the hypotenuse if legs are 7 and 24.",
            "66. In a circle, two chords AB and CD intersect at P. If AP=4, PB=6, CP=3, find PD.",
            "67. What is the volume of a sphere with radius 3?",
            "68. The lateral surface area of a cylinder with radius 5 and height 10 is:",
            "69. Find the length of the space diagonal of a cube with side 4.",
            "70. If the area of a circle is 100π, what is its circumference?",
            "71. Let a triangle have sides 13, 14, 15. Find its area using Heron's formula.",
            "72. What is the exact value of sin(15°)?",
            "73. In a right triangle, the altitude to the hypotenuse divides it into segments of 4 and 9. Find the altitude.",
            "74. What is the circumradius of a triangle with sides 6, 8, 10?",
            "75. Two similar triangles have perimeters in the ratio 2:3. What is the ratio of their areas?",
            "76. An exterior angle of a regular polygon is 24°. How many sides does it have?",
            "77. The distance between points (1, 2, 3) and (4, 6, 15) in 3D space is:",
            "78. What is the area of a regular hexagon with side length 2?",
            "79. If a sphere's surface area is 36π, what is its volume?",
            "80. Find the equation of the line passing through (1,2) perpendicular to y = 3x - 4.",
            // Calculus & Advanced Math (81-100)
            "81. Evaluate the limit as x approaches 0 of (sin x)/x.",
            "82. Find the derivative of f(x) = e^(2x) at x = 0.",
            "83. Evaluate the definite integral of 2x dx from x=0 to x=3.",
            "84. What is the value of the infinite sum 1/1^2 + 1/2^2 + 1/3^2 + ... (Basel problem)?",
            "85. Find the local minimum of f(x) = x^2 - 4x + 7.",
            "86. Determine the dot product of vectors <1, 2, 3> and <4, -5, 6>.",
            "87. Evaluate the limit as x approaches infinity of (1 + 1/x)^x.",
            "88. What is the derivative of ln(x^2)?",
            "89. Find the Taylor series expansion of e^x evaluated at x=1.",
            "90. Evaluate the integral of cos(x) dx from 0 to π/2.",
            "91. If f(x) = x^x, what is f'(1)?",
            "92. Calculate the cross product magnitude of two parallel vectors.",
            "93. What is the curvature of a circle with radius R?",
            "94. Find the Maclaurin series expansion for sin(x) (first term).",
            "95. The trace of the matrix [2, 5; 3, 7] is:",
            "96. Determine the eigenvalues of the matrix [1, 0; 0, -1].",
            "97. Solve the differential equation dy/dx = y. (General solution)",
            "98. The integral of 1/(1+x^2) from 0 to 1 is:",
            "99. What is the sum of the complex roots of z^4 - 1 = 0?",
            "100. Evaluate Euler's Identity: e^(iπ) + 1 = ?"
    };

    private String[][] options = {
            // 1-20
            {"248", "249", "250", "251"},
            {"01", "07", "43", "49"},
            {"1", "2", "3", "4"},
            {"36", "45", "48", "54"},
            {"2^10 - 1", "2^20 - 1", "2^40 - 1", "1"},
            {"10", "40", "50", "100"},
            {"8", "9", "2", "5"},
            {"47", "48", "49", "50"},
            {"0", "2", "4", "Infinite"},
            {"3", "4", "5", "8"},
            {"58", "87", "114", "118"},
            {"17", "257", "31", "127"},
            {"1", "3", "7", "9"},
            {"97", "98", "99", "100"},
            {"217", "100", "210", "150"},
            {"48", "60", "72", "120"},
            {"1", "5", "12", "0"},
            {"2", "3", "4", "5"},
            {"12", "24", "28", "28"},
            {"1", "2", "3", "4"},
            // 21-40
            {"-4", "4", "1", "-6"},
            {"18", "27", "24", "9"},
            {"25", "50", "100", "75"},
            {"-5/2", "5/2", "-5", "2"},
            {"32i", "64i", "-32", "32"},
            {"12", "13", "14", "18"},
            {"4", "5", "6", "2"},
            {"1", "2", "0.5", "Infinity"},
            {"40", "80", "-40", "-80"},
            {"1", "2", "3", "4"},
            {"x", "(x-1)/(x+1)", "-1/x", "-(x+1)/(x-1)"},
            {"(-1, 6)", "(-2, 6)", "(-1, 5)", "(0, 6)"},
            {"-2", "2", "0", "10"},
            {"15050", "15000", "14850", "15100"},
            {"(1+sqrt(5))/2", "(1-sqrt(5))/2", "sqrt(2)", "1.618"},
            {"1/5", "2/5", "3/5", "4/5"},
            {"5/3", "3/5", "2", "4"},
            {"0", "1", "3", "9"},
            {"8", "16", "32", "4"},
            {"0", "1", "2", "-2"},
            // 41-60
            {"24", "60", "120", "240"},
            {"120", "90", "240", "60"},
            {"34650", "110", "121", "34000"},
            {"45", "55", "36", "28"},
            {"252/1024", "1/2", "120/1024", "10/1024"},
            {"35", "45", "40", "50"},
            {"10", "5", "1", "15"},
            {"45", "50", "90", "100"},
            {"48", "52", "24", "1"},
            {"252", "126", "512", "120"},
            {"128", "256", "512", "64"},
            {"24", "120", "12", "60"},
            {"4536", "5040", "3024", "9000"},
            {"40320", "8", "64", "256"},
            {"1/6", "1/12", "1/3", "1/4"},
            {"10", "15", "16", "20"},
            {"75", "125", "60", "25"},
            {"1", "2", "3", "4"},
            {"9", "12", "24", "6"},
            {"1/7", "2/7", "3/7", "1/21"},
            // 61-80
            {"1", "2", "1.5", "2.5"},
            {"9*sqrt(3)", "18", "6*sqrt(3)", "12"},
            {"16π", "64π", "32π", "8π"},
            {"1440°", "1800°", "2160°", "3600°"},
            {"25", "26", "24", "30"},
            {"6", "8", "10", "12"},
            {"27π", "36π", "12π", "9π"},
            {"50π", "100π", "25π", "200π"},
            {"4*sqrt(3)", "8", "12", "4*sqrt(2)"},
            {"10π", "20π", "100π", "50π"},
            {"84", "42", "126", "60"},
            {"(sqrt(6)-sqrt(2))/4", "sqrt(2)/4", "sqrt(3)/2", "(sqrt(6)+sqrt(2))/4"},
            {"5", "6", "7", "36"},
            {"4", "5", "6", "10"},
            {"4:9", "2:3", "1:2", "8:27"},
            {"10", "12", "15", "20"},
            {"13", "14", "15", "12"},
            {"6*sqrt(3)", "12", "8*sqrt(3)", "3*sqrt(3)"},
            {"36π", "18π", "72π", "12π"},
            {"y = -1/3x + 7/3", "y = 3x - 1", "y = -1/3x + 1", "y = -3x + 5"},
            // 81-100
            {"0", "1", "Infinity", "-1"},
            {"1", "2", "e", "0"},
            {"6", "9", "3", "18"},
            {"π^2/6", "π/4", "1", "Infinity"},
            {"3", "7", "4", "0"},
            {"10", "12", "15", "20"},
            {"1", "e", "Infinity", "0"},
            {"1/x", "2/x", "x^2", "2x"},
            {"e", "1", "0", "Infinity"},
            {"1", "0", "π/2", "-1"},
            {"0", "1", "2", "e"},
            {"0", "1", "Infinity", "-1"},
            {"R", "1/R", "R^2", "πR"},
            {"x", "1", "x^2", "0"},
            {"9", "5", "7", "14"},
            {"1, -1", "0, 1", "2, 0", "1, 1"},
            {"y = ce^x", "y = cx", "y = e^c", "y = x^2"},
            {"π/4", "π/2", "1", "0"},
            {"1", "-1", "0", "i"},
            {"0", "1", "-1", "i"}
    };

    private int[] correctAnswers = {
            // 1-20
            1, 0, 1, 1, 1, 1, 1, 1, 0, 0,
            1, 1, 0, 0, 0, 0, 2, 1, 2, 3,
            // 21-40
            1, 0, 1, 0, 0, 1, 0, 0, 1, 2,
            2, 0, 0, 0, 0, 3, 0, 1, 1, 2,
            // 41-60
            2, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            1, 0, 0, 0, 0, 2, 1, 1, 0, 0,
            // 61-80
            0, 0, 0, 1, 0, 1, 1, 1, 0, 1,
            0, 0, 1, 1, 0, 2, 0, 0, 0, 0,
            // 81-100
            1, 1, 1, 0, 0, 1, 1, 1, 0, 0,
            1, 0, 1, 0, 0, 0, 0, 0, 2, 0
    };

    private String[] explanations = {
            // 1-20
            "1000/5 + 1000/25 + 1000/125 + 1000/625 = 200 + 40 + 8 + 1 = 249.",
            "7^4 = 2401 ending in 01. 2024 is divisible by 4, so the cycle ends at 01.",
            "2^3 = 8 ≡ 1 (mod 7). 2^2023 = (2^3)^674 * 2 ≡ 1 * 2 = 2 (mod 7).",
            "3600 = 2^4 * 3^2 * 5^2. Number of divisors = (4+1)(2+1)(2+1) = 45.",
            "GCD(2^a-1, 2^b-1) = 2^GCD(a,b) - 1. GCD(100, 120) = 20, so 2^20 - 1.",
            "φ(100) = 100 * (1 - 1/2) * (1 - 1/5) = 40.",
            "Multiply by 9: 45x ≡ 9 (mod 11) -> x ≡ 9 (mod 11).",
            "100/3 + 100/9 + 100/27 + 100/81 = 33 + 11 + 3 + 1 = 48.",
            "(x-y)(x+y) = 2022. The factors must have the same parity. 2022 is not divisible by 4. Zero solutions.",
            "From 5! onwards, terms are divisible by 15. Sum of 1!+2!+3!+4! = 33. 33 mod 15 = 3.",
            "The only even prime is 2. So p=2, q=29. Product = 58.",
            "Fermat primes are of form 2^(2^n)+1. For n=3, 2^8+1 = 257.",
            "3^4 = 81 ends in 1. 1000 is divisible by 4, so last digit is 1.",
            "100/2 + 100/4 + 100/8 + ... = 50+25+12+6+3+1 = 97.",
            "100 = 2^2 * 5^2. Sum = (2^0+2^1+2^2)(5^0+5^1+5^2) = 7 * 31 = 217.",
            "Divisors = (a+1). So prime^9. The smallest prime is 2, so 2^9 = 512. Wait, 10 = 2*5, so 2^4 * 3^1 = 48.",
            "1000 ≡ 12 ≡ -1 (mod 13). (-1)^2 = 1.",
            "10 ≡ 3 (mod 7). 10^10 ≡ 3^10 ≡ 4. 10^100 ≡ 3^100 ≡ 4. 10^1000 ≡ 3^1000 ≡ 4. Sum = 12 ≡ 5 (mod 7).",
            "A perfect number equals sum of proper divisors. 28 = 1+2+4+7+14.",
            "Modulo 8, the squares are 0, 1, 4. The solutions for x^2 ≡ 1 are 1, 3, 5, 7. So 4 solutions.",
            // 21-40
            "By Vieta's formulas, the sum of roots is -b/a = -(-4)/1 = 4.",
            "x^3 + 1/x^3 = (x + 1/x)^3 - 3(x + 1/x) = 27 - 9 = 18.",
            "By Cauchy-Schwarz or vertex of parabola, min is when x=y=5. 5^2 + 5^2 = 50.",
            "By Vieta's formulas, product of roots in degree 4 is e/a = -5/2.",
            "(1+i)^2 = 2i. Then (2i)^5 = 32 i^5 = 32i.",
            "Vertex is at x = -b/(2a) = -12/(-4) = 3. f(3) = -18 + 36 - 5 = 13.",
            "log_2(x(x-3)) = 2. x^2 - 3x - 4 = 0. Roots are 4, -1. Only 4 is valid.",
            "Sum of infinite geometric series: S = a / (1 - r) = (1/2) / (1 - 1/2) = 1.",
            "Binomial theorem: C(5,3) * (2x)^3 * (-1)^2 = 10 * 8x^3 * 1 = 80x^3.",
            "By AM-GM inequality, a+b+c >= 3 * cube_root(abc) = 3 * 1 = 3.",
            "Evaluating iteratively: f(f(x)) = -1/x. f(f(f(x))) = -(x+1)/(x-1).",
            "-7 < 2x - 5 < 7 -> -2 < 2x < 12 -> -1 < x < 6. So (-1, 6).",
            "Determinant = (1*4) - (2*3) = 4 - 6 = -2.",
            "S_n = n/2 * (2a + (n-1)d) = 50 * (4 + 99*3) = 50 * 301 = 15050.",
            "Golden ratio is defined as (1 + sqrt(5)) / 2.",
            "15 = 3 / (1 - r) -> 1 - r = 3/15 = 1/5 -> r = 4/5.",
            "2^(3x) = 2^5 -> 3x = 5 -> x = 5/3.",
            "The determinant of any identity matrix is always 1.",
            "By AM-GM, x + 4y >= 2*sqrt(x * 4y) = 2*sqrt(4*16) = 2*8 = 16.",
            "By the Polynomial Remainder Theorem, P(1) equals the remainder, which is 2.",
            // 41-60
            "5! = 5 * 4 * 3 * 2 * 1 = 120.",
            "C(10, 3) = 10! / (3! * 7!) = (10 * 9 * 8) / 6 = 120.",
            "11! / (4! * 4! * 2!) = 34650 (since 4 S's, 4 I's, 2 P's).",
            "Using Stars and Bars: C(8+3-1, 3-1) = C(10, 2) = 45.",
            "C(10, 5) * (1/2)^10 = 252 / 1024.",
            "Formula: n(n-3)/2. For n=10: 10(7)/2 = 35.",
            "Binomial coefficient C(5, 2) = 10.",
            "C(10, 2) = (10 * 9) / 2 = 45.",
            "Choose 4 aces (1 way) and 1 other card (48 ways). Total = 48.",
            "Total moves = 10 (5 R, 5 U). C(10, 5) = 252.",
            "Number of subsets is 2^n = 2^8 = 256.",
            "Circular permutations: (n-1)! = 4! = 24.",
            "9 choices for 1st, 9 for 2nd, 8 for 3rd, 7 for 4th: 9*9*8*7 = 4536.",
            "The number of ways is simply 8! = 40320.",
            "Winning rolls: (1,6),(2,5),(3,4),(4,3),(5,2),(6,1) -> 6/36 = 1/6.",
            "Formula for max regions: (n^2 + n + 2)/2. For n=5: (25+5+2)/2 = 16.",
            "Last digit must be 1,3,5 (3 choices). Other digits have 5 choices. 5*5*3 = 75.",
            "Expected value E = n * p = 4 * 0.5 = 2.",
            "Formula: !n = n! * sum((-1)^k / k!). !4 = 9.",
            "(3/7) * (2/6) = 6/42 = 1/7.",
            // 61-80
            "It's a right triangle. Area=6, s=6. r = Area/s = 6/6 = 1.",
            "Area = (side^2 * sqrt(3)) / 4 = 36*sqrt(3)/4 = 9*sqrt(3).",
            "Radius is half the side = 4. Area = πr^2 = 16π.",
            "Sum = (n-2)*180 = 10 * 180 = 1800°.",
            "Pythagorean theorem: sqrt(7^2 + 24^2) = sqrt(49 + 576) = sqrt(625) = 25.",
            "Intersecting Chords Theorem: AP * PB = CP * PD -> 4 * 6 = 3 * PD -> PD = 8.",
            "Volume = (4/3)πr^3 = (4/3) * π * 27 = 36π.",
            "Lateral Area = 2πrh = 2 * π * 5 * 10 = 100π.",
            "Space diagonal = side * sqrt(3) = 4*sqrt(3).",
            "Area = 100π -> r = 10. Circumference = 2πr = 20π.",
            "s = 21. Area = sqrt(21 * 8 * 7 * 6) = 84.",
            "sin(15°) = sin(45°-30°) = (sqrt(6) - sqrt(2)) / 4.",
            "Altitude^2 = segment1 * segment2 = 4 * 9 = 36 -> Altitude = 6.",
            "It's a right triangle, so circumradius is half the hypotenuse: 10/2 = 5.",
            "Ratio of areas is the square of ratio of perimeters: 2^2 : 3^2 = 4:9.",
            "Sum of exterior angles is 360°. 360 / 24 = 15 sides.",
            "Distance = sqrt(3^2 + 4^2 + 12^2) = sqrt(9+16+144) = sqrt(169) = 13.",
            "6 equilateral triangles: 6 * (2^2 * sqrt(3) / 4) = 6*sqrt(3).",
            "Surface Area = 4πr^2 = 36π -> r=3. Volume = (4/3)π(27) = 36π.",
            "Slope is -1/3. y - 2 = -1/3 (x - 1) -> y = -1/3x + 7/3.",
            // 81-100
            "Standard calculus limit: lim x->0 (sin x)/x = 1.",
            "Chain rule: f'(x) = 2e^(2x). f'(0) = 2e^0 = 2.",
            "Integral of 2x is x^2. Evaluated from 0 to 3: 3^2 - 0^2 = 9.",
            "This is the Basel problem, solved by Euler. The sum is π^2/6.",
            "Vertex is at x=2. f(2) = 4 - 8 + 7 = 3.",
            "1*4 + 2*(-5) + 3*6 = 4 - 10 + 18 = 12.",
            "Standard limit definition of the mathematical constant e.",
            "Chain rule: (1/x^2) * 2x = 2/x.",
            "The Taylor series of e^x at x=1 yields the constant e.",
            "Integral of cos(x) is sin(x). sin(π/2) - sin(0) = 1 - 0 = 1.",
            "Using logarithmic differentiation, f'(x) = x^x(ln x + 1). f'(1) = 1(0 + 1) = 1.",
            "The cross product of any two parallel vectors is the zero vector, magnitude 0.",
            "Curvature is defined as the reciprocal of the radius: 1/R.",
            "Maclaurin series for sin(x) is x - x^3/3! + x^5/5!... First term is x.",
            "Trace is the sum of the main diagonal elements: 2 + 7 = 9.",
            "It's a diagonal matrix, so the eigenvalues are just the diagonal entries: 1, -1.",
            "Separation of variables leads to ln|y| = x + C, so y = ce^x.",
            "Arctan(1) - Arctan(0) = π/4 - 0 = π/4.",
            "The sum of all roots of z^n - 1 = 0 is always 0 (coefficient of z^3 is 0).",
            "Euler's identity states e^(iπ) = -1. Therefore, e^(iπ) + 1 = 0."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_final_exam);

        tvProgress = findViewById(R.id.tvProgress);
        tvTimer = findViewById(R.id.tvTimer);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvFeedback = findViewById(R.id.tvFeedback);
        rgOptions = findViewById(R.id.rgOptions);
        rbOption1 = findViewById(R.id.rbOption1);
        rbOption2 = findViewById(R.id.rbOption2);
        rbOption3 = findViewById(R.id.rbOption3);
        rbOption4 = findViewById(R.id.rbOption4);
        btnAction = findViewById(R.id.btnAction);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setMax(questions.length);

        startTimer();
        loadQuestion();

        btnAction.setOnClickListener(v -> {
            if (!isAnswerSubmitted) {
                checkAnswer();
            } else {
                nextQuestion();
            }
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(EXAM_TIME_IN_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                tvTimer.setText(String.format(Locale.US, "⏱ %02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("⏱ 00:00");
                Toast.makeText(AdvFinalExamActivity.this, "Time is up!", Toast.LENGTH_LONG).show();
                finishExam();
            }
        }.start();
    }

    private void loadQuestion() {
        isAnswerSubmitted = false;
        rgOptions.clearCheck();
        enableOptions(true);
        tvFeedback.setVisibility(View.GONE);
        btnAction.setText("Submit Answer");

        tvProgress.setText("Question " + (currentQuestionIndex + 1) + "/" + questions.length);
        progressBar.setProgress(currentQuestionIndex + 1);

        tvQuestion.setText(questions[currentQuestionIndex]);
        rbOption1.setText(options[currentQuestionIndex][0]);
        rbOption2.setText(options[currentQuestionIndex][1]);
        rbOption3.setText(options[currentQuestionIndex][2]);
        rbOption4.setText(options[currentQuestionIndex][3]);

        rbOption1.setTextColor(Color.parseColor("#333333"));
        rbOption2.setTextColor(Color.parseColor("#333333"));
        rbOption3.setTextColor(Color.parseColor("#333333"));
        rbOption4.setTextColor(Color.parseColor("#333333"));
    }

    private void checkAnswer() {
        int selectedId = rgOptions.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedIndex = -1;
        if (selectedId == R.id.rbOption1) selectedIndex = 0;
        else if (selectedId == R.id.rbOption2) selectedIndex = 1;
        else if (selectedId == R.id.rbOption3) selectedIndex = 2;
        else if (selectedId == R.id.rbOption4) selectedIndex = 3;

        int correctIndex = correctAnswers[currentQuestionIndex];

        if (selectedIndex == correctIndex) {
            score++;
            tvFeedback.setText("✅ Brilliant! " + explanations[currentQuestionIndex]);
            tvFeedback.setTextColor(Color.parseColor("#388E3C"));
        } else {
            tvFeedback.setText("❌ Incorrect. " + explanations[currentQuestionIndex]);
            tvFeedback.setTextColor(Color.parseColor("#D32F2F"));

            // Կարմրեցնում ենք սխալ ընտրածը
            if (selectedIndex == 0) rbOption1.setTextColor(Color.RED);
            else if (selectedIndex == 1) rbOption2.setTextColor(Color.RED);
            else if (selectedIndex == 2) rbOption3.setTextColor(Color.RED);
            else if (selectedIndex == 3) rbOption4.setTextColor(Color.RED);
        }

        // Կանաչեցնում ենք ճիշտ տարբերակը
        if (correctIndex == 0) rbOption1.setTextColor(Color.parseColor("#388E3C"));
        else if (correctIndex == 1) rbOption2.setTextColor(Color.parseColor("#388E3C"));
        else if (correctIndex == 2) rbOption3.setTextColor(Color.parseColor("#388E3C"));
        else if (correctIndex == 3) rbOption4.setTextColor(Color.parseColor("#388E3C"));

        tvFeedback.setVisibility(View.VISIBLE);
        enableOptions(false);
        isAnswerSubmitted = true;

        if (currentQuestionIndex == questions.length - 1) {
            btnAction.setText("Finish Exam");
        } else {
            btnAction.setText("Next Question");
        }
    }

    private void enableOptions(boolean enable) {
        for (int i = 0; i < rgOptions.getChildCount(); i++) {
            rgOptions.getChildAt(i).setEnabled(enable);
        }
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.length) {
            loadQuestion();
        } else {
            finishExam();
        }
    }

    private void finishExam() {
        if (countDownTimer != null) countDownTimer.cancel();

        SharedPreferences myPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // 80% շեմը = 80 հարց 100-ից
        if (score >= 80) {
            myPrefs.edit().putBoolean("adv_exam_passed", true).apply();
        }

        myPrefs.edit().putInt("total_stars", myPrefs.getInt("total_stars", 0) + score).apply();

        Intent intent = new Intent(AdvFinalExamActivity.this, AdvResultActivity.class);
        intent.putExtra("total_score", score);
        intent.putExtra("max_score", questions.length);
        startActivity(intent);
        finish();
    }
}



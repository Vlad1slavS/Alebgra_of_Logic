import java.util.*;

public class QuineMcCluskey {
    // Список двоичных строк, представляющих минтермы.
    private List<String> minterms;
    // Список строк, представляющих существенные импликанты.
    private List<String> essentialImplicants;

    // Конструктор, принимающий список минтермов и количество переменных.
    public QuineMcCluskey(List<Integer> mintermList, int numVars) {
        minterms = new ArrayList<>();

        // Конвертируем каждый минтерм в двоичную строку с заданной длиной.
        for (Integer minterm : mintermList) {
            minterms.add(toBinaryString(minterm, numVars));
        }

        // Инициализируем список для хранения существенных импликантов.
        essentialImplicants = new ArrayList<>();
    }

    // Метод для минимизации логической функции.
    public void minimize() {
        // Получаем список всех простых импликантов.
        List<String> primeImplicants = getPrimeImplicants();
        // Находим среди них существенные импликанты.
        essentialImplicants = findEssentialImplicants(primeImplicants);
    }

    // Конвертация целочисленного значения в двоичную строку заданной длины.
    private String toBinaryString(int value, int length) {
        // Используем форматированную строку для дополнения нулями до нужной длины.
        return String.format("%" + length + "s", Integer.toBinaryString(value))
                .replace(' ', '0');
    }

    // Метод для поиска простых импликантов из минтермов.
    private List<String> getPrimeImplicants() {
        // Создаем список импликантов, начиная из минтермов.
        List<String> result = new ArrayList<>(minterms);
        List<String> next = new ArrayList<>();
        boolean[] merged = new boolean[minterms.size()];

        // Цикл, продолжающийся, пока происходит успешное объединение импликантов.
        while (true) {
            next.clear();
            Arrays.fill(merged, false);

            // Попарное сравнение и попытки объединить импликанты.
            for (int i = 0; i < result.size(); i++) {
                for (int j = i + 1; j < result.size(); j++) {
                    String implicant1 = result.get(i);
                    String implicant2 = result.get(j);
                    // Пробуем объединить импликанты, если они различаются лишь в одной позиции.
                    String combined = combine(implicant1, implicant2);
                    if (combined != null) {
                        next.add(combined);
                        merged[i] = true;
                        merged[j] = true;
                    }
                }
            }

            // Добавляем неиспользованные импликанты во временный список.
            for (int i = 0; i < result.size(); i++) {
                if (!merged[i] && !next.contains(result.get(i))) {
                    next.add(result.get(i));
                }
            }

            // Если больше не происходит объединений, заканчиваем цикл.
            if (result.containsAll(next) && next.containsAll(result)) {
                break;
            }

            // Обновляем список импликантов для следующей итерации.
            result.clear();
            result.addAll(next);
        }

        // Возвращаем список простых импликантов.
        return result;
    }

    // Объединение двух импликантов, если они различаются в одной позиции.
    private String combine(String a, String b) {
        int differences = 0;
        StringBuilder sb = new StringBuilder();

        // Перебираем каждый бит для объединения.
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) {
                differences++;
                sb.append('-');  // Обозначаем различие знаком '-'.
            } else {
                sb.append(a.charAt(i));  // Совпадающие биты остаются такими же.
            }
        }

        // Возвращаем объединенный импликант, если они различаются только в одной позиции.
        return differences == 1 ? sb.toString() : null;
    }

    // Нахождение существенных импликантов из списка простых.
    private List<String> findEssentialImplicants(List<String> primeImplicants) {
        List<String> essentials = new ArrayList<>();

        // Для каждого минтерма ищем импликанты, которые его покрывают.
        for (String minterm : minterms) {
            int count = 0;
            String temp = null;
            for (String implicant : primeImplicants) {
                // Проверяем, покрывает ли импликант данный минтерм.
                if (matches(implicant, minterm)) {
                    count++;
                    temp = implicant;
                }
            }

            // Если минтерм покрывается только одним импликантом — это существенный импликант.
            if (count == 1 && !essentials.contains(temp)) {
                essentials.add(temp);
            }
        }

        // Возвращаем список существенных импликантов.
        return essentials;
    }

    // Проверка, покрывает ли один импликант конкретный минтерм.
    private boolean matches(String implicant, String minterm) {
        // Проверяем каждый символ, чтобы убедиться в совпадении.
        for (int i = 0; i < implicant.length(); i++) {
            if (implicant.charAt(i) != '-' && implicant.charAt(i) != minterm.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    // Конвертация бинарного представления импликанта в логическую функцию.
    private String convertToExpression(String binary) {
        StringBuilder expression = new StringBuilder();
        char[] variables = {'A', 'B', 'C', 'D'};

        // Перебираем каждую позицию в бинарной строке.
        for (int i = 0; i < binary.length(); i++) {
            if (binary.charAt(i) == '1') {
                expression.append(variables[i]); // Добавляем переменную.
            } else if (binary.charAt(i) == '0') {
                expression.append(variables[i]).append("'"); // Добавляем отрицание переменной.
            }
        }
        // Возвращаем строку в виде логического выражения.
        return expression.toString();
    }

    public void printResult() {
        System.out.println("Минимизированная функция:");
        List<String> expressions = new ArrayList<>();
        // Преобразуем каждый существенный импликант в логическое выражение.
        for (String implicant : essentialImplicants) {
            expressions.add("( " + convertToExpression(implicant) + " )");
        }
        // Выводим результаты в виде дизъюнкции логических выражений.
        System.out.println(String.join(" ∨ ", expressions ));
    }

    public static void main(String[] args) {
        List<Integer> minterms = Arrays.asList(2, 3, 5, 6, 9);
        // Создание нового объекта QuineMcCluskey с заданными минтермами.
        QuineMcCluskey qmc = new QuineMcCluskey(minterms, 4);
        qmc.minimize(); // Запускаем процесс минимизации.
        qmc.printResult(); // Печатаем результат.
    }
}

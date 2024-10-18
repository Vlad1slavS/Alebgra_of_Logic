import java.util.*;

public class QuineMcCluskey {
    // Список двоичных строк, представляющих минтермы.
    private List<String> minterms;
    // Список строк, представляющих существенные импликанты.
    private List<String> essentialImplicants;
    private Integer numVars;

    // Конструктор, принимающий список минтермов и количество переменных.
    public QuineMcCluskey(String truthTableString) {
        this.numVars = (int) (Math.log(truthTableString.length()) / Math.log(2));
        this.minterms = new ArrayList<>();
        this.essentialImplicants = new ArrayList<>();

        for (int i = 0; i < truthTableString.length(); i++) {
            if (truthTableString.charAt(i) == '1') {
                minterms.add(toBinaryString(i, numVars));
            }
        }
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
        boolean[] coveredMinterms = new boolean[minterms.size()];

        // Проходим по каждому минтерму
        for (int i = 0; i < minterms.size(); i++) {
            String minterm = minterms.get(i);
            int count = 0;
            String coveringImplicant = null;

            // Ищем импликанты, которые покрывают данный минтерм.
            for (String implicant : primeImplicants) {
                if (matches(implicant, minterm)) {
                    count++;
                    coveringImplicant = implicant;
                }
            }

            // Если минтерм покрывается только одним импликантом, то этот импликант - существенный.
            if (count == 1 && !essentials.contains(coveringImplicant)) {
                essentials.add(coveringImplicant);
                // Отмечаем все минтермы, покрываемые этим существенным импликантом.
                for (int j = 0; j < minterms.size(); j++) {
                    if (matches(coveringImplicant, minterms.get(j))) {
                        coveredMinterms[j] = true;
                    }
                }
            }
        }

        // Добавьте импликанты для непокрытых минтермов (для полной минимизации)
        for (int i = 0; i < coveredMinterms.length; i++) {
            if (!coveredMinterms[i]) {
                // Находим первый импликант, покрывающий этот минтерм
                for (String implicant : primeImplicants) {
                    if (matches(implicant, minterms.get(i))) {
                        if (!essentials.contains(implicant)) {
                            essentials.add(implicant);
                        }
                        break;
                    }
                }
            }
        }
        System.out.println(essentials);
        return essentials;
    }

    // Проверка, покрывает ли один импликант конкретный минтерм.
    private boolean matches(String implicant, String minterm) {
        for (int i = 0; i < implicant.length(); i++) {
            if (implicant.charAt(i) != '-' && implicant.charAt(i) != minterm.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private String convertToExpression(String binary) {
        StringBuilder expression = new StringBuilder();
        char[] variables = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        if (numVars > variables.length) {
            throw new IllegalArgumentException("Превышено максимальное количество переменных: " + variables.length);
        }

        for (int i = 0; i < binary.length(); i++) {
            if (binary.charAt(i) == '1') {
                expression.append(variables[i]);
            } else if (binary.charAt(i) == '0') {
                expression.append(variables[i]).append("'");
            }
        }
        return expression.toString();
    }

    public void printResult() {
        System.out.println("Минимизированная функция:");
        if (essentialImplicants.isEmpty()) {
            System.out.println("0"); // Вывод "0", если нет существенных импликантов
            return;
        }

        List<String> expressions = new ArrayList<>();
        for (String implicant : essentialImplicants) {
            expressions.add("( " + convertToExpression(implicant) + " )");
        }
        System.out.println(String.join(" ∨ ", expressions));
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите строку значений функции (0 и 1): ");
        String truthTableString = scanner.nextLine();

        // Проверка на корректность ввода (только 0 и 1)
        if (!truthTableString.matches("[01]+")) {
            System.out.println("Ошибка: в строке должны быть только символы '0' и '1'.");
            return;
        }

        // Проверка на количество значений (2^n)
        if ((truthTableString.length() & (truthTableString.length() - 1)) != 0) {
            System.out.println("Ошибка: длина строки должна быть степенью двойки.");
            return;
        }
//        List<Integer> minterms = Arrays.asList(2, 3, 5, 6, 9);
        // Создание нового объекта QuineMcCluskey с заданными минтермами.
        QuineMcCluskey qmc = new QuineMcCluskey(truthTableString);
        qmc.minimize(); // Запускаем процесс минимизации.
        qmc.printResult(); // Печатаем результат.
    }
}

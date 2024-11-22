public class TimSort {

    private static final int MIN_MERGE = 32;
    private static final int GALLOP_THRESHOLD = 7;

    public static void sort(int[] arr) {
        int n = arr.length;
        // Подсчет минимального run
        int minRun = getMinRun(n);

        // Сортировка массива с помощью insertion sort по run-ам
        for (int i = 0; i < n; i += minRun) {
            insertionSort(arr, i, Math.min(i + minRun, n));
        }

        // Слияние run-ов
        for (int size = minRun; size < n; size *= 2) {
            for (int left = 0; left < n; left += size * 2) {
                int mid = Math.min(left + size, n);
                int right = Math.min(left + size * 2, n);
                merge(arr, left, mid, right);
            }
        }
    }

    // Подсчет минимального run
    private static int getMinRun(int n) {
        int r = 0;
        while (n >= MIN_MERGE) {
            r |= n & 1;
            n >>= 1;
        }
        return n + r;
    }

    // Сортировка вставками
    private static void insertionSort(int[] arr, int left, int right) {
        for (int i = left + 1; i < right; i++) {
            int temp = arr[i];
            int j = i - 1;
            while (j >= left && arr[j] > temp) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = temp;
        }
    }

    // Слияние run-ов
    private static void merge(int[] arr, int left, int mid, int right) {
        int n1 = mid - left;
        int n2 = right - mid;

        int[] leftArr = new int[n1];
        int[] rightArr = new int[n2];

        for (int i = 0; i < n1; i++) {
            leftArr[i] = arr[left + i];
        }
        for (int i = 0; i < n2; i++) {
            rightArr[i] = arr[mid + i];
        }

        int i = 0, j = 0, k = left;
        int gallopCountRight = 0;
        int gallopCountLeft = 0;

        // Галоп
        while (i < n1 && j < n2) {
            if (leftArr[i] <= rightArr[j]) {
                arr[k++] = leftArr[i++];
                gallopCountRight = 0;
                gallopCountLeft++;

                if (gallopCountLeft >= GALLOP_THRESHOLD) {
                    arr[k++] = leftArr[i++];
                    gallopCountLeft++;
                    gallopCountRight = 0;

                    // Переход в режим галопа
                    if (gallopCountRight >= GALLOP_THRESHOLD) {
                        gallopCountRight = 0;

                        int gallopIndex = gallopRight(rightArr, j, n2, leftArr[i]);
                        j += gallopIndex;
                    }
                }
            } else {
                arr[k++] = rightArr[j++];
                gallopCountRight++;
                gallopCountLeft = 0;

                // Переход в режим галопа
                if (gallopCountRight >= GALLOP_THRESHOLD) {
                    gallopCountRight = 0;

                    int gallopIndex = gallopRight(rightArr, j, n2, leftArr[i]);
                    j += gallopIndex;
                }
            }
        }

        // Копирование оставшихся элементов
        while (i < n1) {
            arr[k++] = leftArr[i++];
        }
        while (j < n2) {
            arr[k++] = rightArr[j++];
        }
    }

    // Галоп вправо
    private static int gallopRight(int[] arr, int start, int end, int key) {
        int index = start;
        int step = 1;
        while (index < end && arr[index] <= key) {
            index += step;
            step *= 2;
        }

        return binarySearch(arr, start, Math.min(index, end), key) - start;
    }

    // Двоичный поиск
    private static int binarySearch(int[] arr, int start, int end, int key) {
        while (start < end) {
            int mid = start + (end - start) / 2;
            if (arr[mid] < key) {
                start = mid + 1;
            } else {
                end = mid;
            }
        }
        return start;
    }
}
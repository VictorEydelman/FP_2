### Лабораторная работа №2

Выполнил: Эйдельман Виктор Аркадьевич</br>
Вариант: bt-bag

## Решение:

Для создания структуры Binary Tree с интерфейсом Bag, использовался defrecord с последовательностю value, count, left, right
```clojure
(defn create-bag [] {})

(defrecord Node [value count left right])

(defn create-node [value]
  (->Node value 1 {} {}))
```

1) Добавление:
```clojure
(defn add [node value]
  (if (zero? (count node))
    (create-node value)
    (let [cmp (compare (cond (or (number? value) (nil? value)) value
                             (boolean? value) (if (true? value) 1 0)
                             :else (int value))
                       (cond (or (number? (:value node)) (nil? (:value node))) (:value node)
                             (boolean? (:value node)) (if (true? (:value node)) 1 0)
                             :else (int (:value node))))]
      (cond
        (= cmp 0) (assoc node :count (inc (:count node)))
        (< cmp 0) (assoc node :left (add (:left node) value))
        (> cmp 0) (assoc node :right (add (:right node) value))))))
```
Для добавления используется пербор элементов с помощью assoc и прохода по ветками Binary Tree,
ветки фомируеются по принципу больше или меньше ли наше значение чем, то на котором мы в нашем
дереве сейчас, если элементы равны, но увеличиваем count.

2) Удаление:

```clojure
(defn delete [node value]
  (if (= {} node)
    {}
    (let [cmp (compare (cond (or (number? value) (nil? value)) value
                             (boolean? value) (if (true? value) 1 0)
                             :else (int value))
                       (cond (or (number? (:value node)) (nil? (:value node))) (:value node)
                             (boolean? (:value node)) (if (true? (:value node)) 1 0)
                             :else (int (:value node))))]
      (cond
        (= cmp 0)
        (if (> (:count node) 1)
          (assoc node :count (dec (:count node)))
          (cond
            (= {} (:left node)) (:right node)
            (= {} (:right node)) (:left node)
            :else
            (let [min-node (if (not (= {} (:left node))) (recur (:left node)) node)]
              (assoc (assoc node :value (:value min-node) :count 1)
                :right (delete (:right node) (:value min-node))))))
        (< cmp 0) (assoc node :left (delete (:left node) value))
        (> cmp 0) (assoc node :right (delete (:right node) value))))))
```

Для удаления элемента производится проход по дереву с переходом на левую или правую
ветвь взависимости от того больше или меньше наш элемент, если найден нужный,
то если его количество больше одного, то количество уменьшается на 1, если количество равно одному,
то вместо этого элемента подставляется правая ветвь, если левой нет, и наоборот, если нету правой,
то левую, но если есть и та и та, то ищется минимальное значение в правой ветви и подставляем вместо
удалённого, и из правой ветви удаляем этот минимальный элемент.

3) Фильтрация:

```clojure
(defn filter_f
  [f node]
  (if (= {} node)
    {}
    (cond (f (:value node)) (assoc node :right (filter_f f (:right node)) :left (filter_f f (:left node)))
          :else (cond
                  (= {} (:left node)) (filter_f f (:right node))
                  (= {} (:right node)) (filter_f f (:left node))
                  :else
                  (let [min-node (find-min (:right node))]
                    (filter_f f (assoc (assoc node :value (:value min-node) :count 1)
                                :right (delete (:right node) (:value min-node)))))))))
```

Для фильтрации используется проход по всему дереву, с выводом всех элементов
для которых функция выполняется, а остальные удаляются с помощью функции
delete описаной ранее.

3.1) Фильтрация всех элементов по значению меньше или равному данному:

```clojure
(defn filter_min_or_equal
  ([node value]
   (if (= {} node)
     {}
     (let [cmp (compare (cond (or (number? value) (nil? value)) value
                              (boolean? value) (if (true? value) 1 0)
                              :else (int value))
                        (cond (or (number? (:value node)) (nil? (:value node))) (:value node)
                              (boolean? (:value node)) (if (true? (:value node)) 1 0)
                              :else (int (:value node))))]
       (cond (>= cmp 0) (assoc node :right (filter_min_or_equal (:right node) value))
             (< cmp 0) (filter_min_or_equal (:left node) value))))))
```

Для этого проводится проход по дереву, если значение меньше или равно, то оставляем это значение и переход на правую ветвь, иначе переходим на левую, пропуская данную.

3.2) Фильтрация всех элементов по значению больше или равному данному:

```clojure
(defn filter_max_or_equal
  ([node value]
   (if (= {} node)
     {} (let [cmp (compare (cond (or (number? value) (nil? value)) value
                              (boolean? value) (if (true? value) 1 0)
                              :else (int value))
                        (cond (or (number? (:value node)) (nil? (:value node))) (:value node)
                              (boolean? (:value node)) (if (true? (:value node)) 1 0)
                              :else (int (:value node))))]
       (cond (<= cmp 0) (assoc node :left (filter_max_or_equal (:left node) value))
             (> cmp 0) (filter_max_or_equal (:right node) value))))))
```

Аналогично предыдущему, но наоборот проверяет, что значение больше или равно и переход по ветвям обратный.

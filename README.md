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

# 1) Добавление:
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

# 2) Удаление:

```clojure
(defn find-min [node]
  (if (not (= {} (:left node)))
    (recur (:left node))
    node))

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
            (let [min-node (find-min (:right node))]
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

# 3) Фильтрация:

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

# 3.2) Фильтрация всех элементов по значению меньше или равному данному:

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

# 3.3) Фильтрация всех элементов по значению больше или равному данному:

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

# 4) отображение (map):

```clojure
(defn update-node [value count left right]
  (->Node value count left right))

(defn map_f [node f]
  (if (= {} node)
    {}
    (let [new-value (f (:value node))
          count (:count node)
          left-child (map_f (:left node) f)
          right-child (map_f (:right node) f)]
      (update-node new-value count left-child right-child))))
```

Для map используется проход по дереву и каждый элемент обновляется его, задавая новое значение которое равно f от предыдущего.

# 5.1)свертки левая:

```clojure
(defn reduce_left [node f init]
  (if (= {} node)
    init
    (let [init2 (reduce_left (:left node) f init)]
      (reduce_left (:right node) f (loop [count (dec (:count node)) init3 (f init2 (:value node))]
         (if (zero? count) init3 (recur (dec count) (f init3 (:value node))))))))
  )
```

Для этого идёт проход слева направо и идёт сверка значения элемента с предыдущим результатом.

# 5.2)свертки правая:

```clojure
defn reduce_right [node f init]
  (if (= {} node)
    init
    (let [init2 (reduce_right (:right node) f init)]
      (reduce_right (:left node) f
                    (loop [count (dec (:count node)) init3 (f init2 (:value node))]
                      (if (zero? count) init3 (recur (dec count) (f init3 (:value node)))))
                    ))))
```

Тоже самое, что и при левой, но проход идёт справа налево.

# 6) Объединение двух bag в один:

```clojure
(defn merger [bag1 bag2]
  (reduce_left bag2 add bag1))
```

Для этого использую свёртку слева при которой из второго bag элементы добавляюся в первый c помощью команды add.

## unit testing:

```clojure
(ns unitTest
  (:require [clojure.test :refer [deftest is run-tests]]
            [a :refer [create-bag add delete filter_f filter_min_or_equal filter_max_or_equal map_f reduce_left reduce_right merger]]))
(deftest test_add
  (let [bag (create-bag)]
    (is (= (add bag 2) #a.Node{:value 2, :count 1, :left {}, :right {}}))

    (is (= (add (add (add (add bag 1) 1) 2) 0) #a.Node{:value 1, :count 2,
                                                       :left  #a.Node{:value 0, :count 1, :left {}, :right {}},
                                                       :right #a.Node{:value 2, :count 1, :left {}, :right {}}}))))
(deftest test_delete
  (let [bag (add (add (add (add (create-bag) 1) 1) 2) 0)]
    (is (= (delete bag 1) #a.Node{:value 1, :count 1,
                                  :left  #a.Node{:value 0, :count 1, :left {}, :right {}},
                                  :right #a.Node{:value 2, :count 1, :left {}, :right {}}}))

    (is (= (delete bag 2) #a.Node{:value 1, :count 2,
                                  :left  #a.Node{:value 0, :count 1, :left {}, :right {}},
                                  :right {}}))
    (is (= (delete (delete bag 1) 1) #a.Node{:value 2, :count 1,
                                             :left  #a.Node{:value 0, :count 1, :left {}, :right {}},
                                             :right {}}))))

(deftest test_filter_min_or_equal
  (let [bag (add (add (add (add (create-bag) 1) 1) 2) 0)]
    (is (= (filter_min_or_equal bag 1) #a.Node{:value 1, :count 2,
                                               :left #a.Node{:value 0, :count 1, :left {}, :right {}},
                                               :right {}}))))
(deftest test_filter_f_max_or_equal
  (let [bag (add (add (add (add (create-bag) 1) 1) 2) 0)]
    (is (= (filter_max_or_equal bag 1) #a.Node{:value 1, :count 2,
                                               :left {},
                                               :right #a.Node{:value 2, :count 1, :left {}, :right {}}}))))
(defn prost? [num]
  (loop [i (int (Math/sqrt num))]
    (if (zero? (rem num i))
      (= i 1)
      (recur (dec i)))))

(deftest test_filter_f
  (let [bag (add (add (add (add (create-bag) 3) 5) 2) 3)]
    (is (= (filter_f prost? bag) #a.Node{:value 3, :count 2, :left #a.Node{:value 2, :count 1, :left {}, :right {}}, :right #a.Node{:value 5, :count 1, :left {}, :right {}}})))
  (let [bag (add (add (add (add (create-bag) 1) 1) 2) 0)]
    (is (= (filter_f zero? bag) #a.Node{:value 0, :count 1, :left {}, :right {}}))))

(deftest test_map_f
  (let [bag (add (add (add (add (create-bag) 1) 1) 2) 0)]
    (is (= (map_f bag inc) #a.Node{:value 2, :count 2,
                                 :left  #a.Node{:value 1, :count 1, :left {}, :right {}},
                                 :right #a.Node{:value 3, :count 1, :left {}, :right {}}}))))

(deftest test_reduce_left
  (let [bag (add (add (add (add (create-bag) 1) 1) 2) 0)]
    (is (= (reduce_left bag + 0) 4))
    (is (= (reduce_left bag max 0) 2))
    (is (= (reduce_left bag min 100000000000) 0))))

(deftest test_reduce_right
  (let [bag (add (add (add (add (create-bag) 1) 1) 2) 0)]
    (is (= (reduce_right bag + 0) 4))
    (is (= (reduce_right bag max 0) 2))
    (is (= (reduce_right bag min 100000000000) 0))))

(deftest test_merger
  (let [bag1 (add (add (add (add (create-bag) 1) 1) 2) 0) bag2 (add (add (add (add (create-bag) 3) 5) 2) 3)]
    (is (= (merger bag1 bag2) #a.Node{:value 1, :count 2, :left
                                      #a.Node{:value 0, :count 1, :left {}, :right {}},
                                      :right #a.Node{:value 2, :count 2, :left {}, :right #a.Node{:value 3, :count 2, :left {}, :right #a.Node{:value 5, :count 1, :left {}, :right {}}}}}))
    ))

(run-tests)
```

В нём тестируются все описанные ранее методы.

## property-based тестирование:

```clojure
(ns propertyTest
  (:require
    [clojure.test.check.clojure-test :refer [defspec]]
    [clojure.test.check.generators :as gen]
    [clojure.test.check.properties :as prop]
    [a :refer [add delete create-bag merger]]))

(def gen_element
  gen/int)

(def gen_bag
  (gen/vector gen_element))

#_{:clj-kondo/ignore [:unresolved-symbol]}
(defspec invariant
  100
  (prop/for-all [element gen_element node gen_bag]
    (let [bag (if (= node []) (create-bag) (reduce add (create-bag) node))]
      (= (delete (add bag element) element) bag))
    ))

#_{:clj-kondo/ignore [:unresolved-symbol]}
(defspec merge-empty-bag
  100
  (prop/for-all [node gen_bag]
    (let [bag (if (= node []) (create-bag) (reduce add (create-bag) node))]
      (= (merger bag (create-bag)) bag))
    ))
#_{:clj-kondo/ignore [:unresolved-symbol]}
(defspec monoid
  100
  (prop/for-all [node gen_bag]
    (let [bag (if (= node []) (create-bag) (reduce add (create-bag) node))]
      (= (merger (merger bag bag) bag)
         (merger bag (merger bag bag))))
    ))
#_{:clj-kondo/ignore [:unresolved-symbol]}
(defspec polymorphic
  100
  (prop/for-all [element (gen/one-of [gen/int gen/char gen/boolean]) node (gen/vector (gen/one-of [gen/int gen/char gen/boolean]))]
    (let [bag (if (= node []) (create-bag) (reduce add (create-bag) node))]
      (= (delete (add bag element) element) bag))
    ))
```

Это тестирование проверяет выполнение различных свойств для библиотеки.

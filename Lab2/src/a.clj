(ns a)
(defn create-bag [] {})

(defrecord Node [value count left right])

(defn create-node [value]
  (->Node value 1 nil nil))

(defn add [node value]
  (if (zero? (count node))
    (create-node value)
    (let [cmp (compare value (:value node))]
      (cond
        (= cmp 0) (assoc node :count (inc (:count node)))
        (< cmp 0) (assoc node :left (add (:left node) value))
        (> cmp 0) (assoc node :right (add (:right node) value))))))

(defn find-min [node]
  (if (:left node)
    (recur (:left node))
    node))

(defn delete [node value]
  (if (nil? node)
    nil
    (let [cmp (compare value (:value node))]
      (cond
        (= cmp 0)
        (if (> (:count node) 1)
          (assoc node :count (dec (:count node)))
          ;; Удаляем узел
          (cond
            (nil? (:left node)) (:right node)
            (nil? (:right node)) (:left node)
            :else
            (let [min-node (find-min (:right node))]
              (assoc (assoc node :value (:value min-node) :count 1)
                :right (delete (:right node) (:value min-node))))))
        (< cmp 0) (assoc node :left (delete (:left node) value))
        (> cmp 0) (assoc node :right (delete (:right node) value))))))

(defn filter_min_or_equal
  ([node value]
   (if (nil? node)
     nil
     (let [cmp (compare value (:value node))]
       (cond (>= cmp 0) (assoc node :right (filter_min_or_equal (:right node) value))
             (< cmp 0) (filter_min_or_equal (:left node) value))))))
(defn filter_max_or_equal
  ([node value]
   (if (nil? node)
     nil
     (let [cmp (compare value (:value node))]
       (cond (<= cmp 0) (assoc node :left (filter_max_or_equal (:left node) value))
             (> cmp 0) (filter_max_or_equal (:right node) value))))))


(defn update-node [value count left right]
  (->Node value count left right))

(defn map_f [node f]
  (if (nil? node)
    nil
    (let [new-value (f (:value node))
          count (:count node)
          left-child (map_f (:left node) f)
          right-child (map_f (:right node) f)]
      (update-node new-value count left-child right-child))))

(defn reduce_left [node f init]
  (if (nil? node)
    init
    (let [init2 (reduce_left (:left node) f init)]
      (let [init3 (loop [count (dec (:count node)) init3 (f init2 (:value node))]
                    (if (zero? count) init3 (recur (dec count) (f init3 (:value node)))))]
        (reduce_left (:right node) f init3))))
  )
(defn reduce_right [node f init]
  (if (nil? node)
    init
    (let [init2 (reduce_right (:right node) f init)]
      (let [init3 (loop [count (dec (:count node)) init3 (f init2 (:value node))]
                    (if (zero? count) init3 (recur (dec count) (f init3 (:value node)))))]
        (reduce_right (:left node) f init3))))
  )

;(defn add_bag_to_bag
 ; [bag1 bag2]
  ;(add bag1 (:value bag2))
  ;(println bag1 bag2 1)
  ;(if (nil? bag2)
   ; nil
    ;(let [new-value (:value bag2)
     ;     left-child (add_bag_to_bag bag1 (:left bag2))
      ;    right-child (add_bag_to_bag bag1 (:right bag2) )]
      ;bag1)))
;(def b (add (add (create-bag) 2) 0))
;(def b2 (add (add (create-bag) -1) 4))
;(def b (add_bag_to_bag b b2))
;(println b)

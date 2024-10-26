(ns a)
(defn create-bag [] {})

(defrecord Node [value count left right])

(defn create-node [value]
  (->Node value 1 {} {}))

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

(defn reduce_left [node f init]
  (if (= {} node)
    init
    (let [init2 (reduce_left (:left node) f init)]
      (reduce_left (:right node) f (loop [count (dec (:count node)) init3 (f init2 (:value node))]
         (if (zero? count) init3 (recur (dec count) (f init3 (:value node))))))))
  )

(defn reduce_right [node f init]
  (if (= {} node)
    init
    (let [init2 (reduce_right (:right node) f init)]
      (reduce_right (:left node) f
         (loop [count (dec (:count node)) init3 (f init2 (:value node))]
            (if (zero? count) init3 (recur (dec count) (f init3 (:value node)))))
        )))
  )

(defn merger [bag1 bag2]
  (reduce_left bag2 add bag1))


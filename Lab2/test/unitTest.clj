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
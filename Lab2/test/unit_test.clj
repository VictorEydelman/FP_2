(ns unit_test
  (:require [clojure.test :refer [deftest testing is run-tests]]
            [a :refer [create-bag add remove filter_min_or_equal filter_max_or_equal map reduce_left reduce_right]]))
(deftest test_add
  (let [bag (create-bag)]
    (is (= (add bag 2) #a.Node{:value 2, :count 1, :left nil, :right nil}))

    (is (= (add (add (add (add bag 1) 1) 2) 0) #a.Node{:value 1, :count 2,
                                                       :left  #a.Node{:value 0, :count 1, :left nil, :right nil},
                                                       :right #a.Node{:value 2, :count 1, :left nil, :right nil}}))))
(deftest test_remove
  (let [bag (add (add (add (add (create-bag) 1) 1) 2) 0)]
    (is (= (remove bag 1) #a.Node{:value 1, :count 1,
                                  :left  #a.Node{:value 0, :count 1, :left nil, :right nil},
                                  :right #a.Node{:value 2, :count 1, :left nil, :right nil}}))

    (is (= (remove bag 2) #a.Node{:value 1, :count 2,
                                  :left  #a.Node{:value 0, :count 1, :left nil, :right nil},
                                  :right nil}))
    (is (= (remove (remove bag 1) 1) #a.Node{:value 2, :count 1,
                                             :left  #a.Node{:value 0, :count 1, :left nil, :right nil},
                                             :right nil}))))

(deftest test_find_min_or_equal
  (let [bag (add (add (add (add (create-bag) 1) 1) 2) 0)]
    (is (= (filter_min_or_equal bag 1) #a.Node{:value 1, :count 2,
                                               :left #a.Node{:value 0, :count 1, :left nil, :right nil},
                                               :right nil}))))
(deftest test_find_max_or_equal
  (let [bag (add (add (add (add (create-bag) 1) 1) 2) 0)]
    (is (= (filter_max_or_equal bag 1) #a.Node{:value 1, :count 2,
                                               :left nil,
                                               :right #a.Node{:value 2, :count 1, :left nil, :right nil}}))))

(deftest test_map
  (let [bag (add (add (add (add (create-bag) 1) 1) 2) 0)]
    (is (= (map bag inc) #a.Node{:value 2, :count 2,
                                 :left  #a.Node{:value 1, :count 1, :left nil, :right nil},
                                 :right #a.Node{:value 3, :count 1, :left nil, :right nil}}))))

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

(run-tests)
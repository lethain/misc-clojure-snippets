(ns test 
  (:require vec)
  (:use clojure.contrib.test-is))

(deftest test-cross-product
  (is (= [-3 6 -3] (vec/cross [1 2 3] [4 5 6])))
  (is (= [0 0 1]   (vec/cross [1 0 0] [0 1 0]))))

(run-tests)
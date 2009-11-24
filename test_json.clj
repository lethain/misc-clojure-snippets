(ns test-json 
  (:require clojure.contrib.json.read)
  (:use clojure.contrib.test-is))

(deftest test-json-something
  (is (= 1 2))
  (is (read-json "[]") 3))

(run-tests)
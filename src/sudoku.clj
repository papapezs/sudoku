(ns sudoku
  (:require [clojure.set :as set]))

(def board identity)

(def all-values #{1 2 3 4 5 6 7 8 9})

(defn value-at [board coord]
  (get-in board coord))

(defn has-value? [board coord]
  (not= 0 (value-at board coord)))

(defn row-values [board coord]
  (let [[r c] coord]
    (set (distinct (for [x (range 9)] (value-at board [r x]))))))

(defn col-values [board coord]
  (let [[r c] coord]
    (set (distinct (for [x (range 9)] (value-at board [x c]))))))

(defn coord-pairs [coords]
  (apply concat (for [x coords] (for [y coords] [x y]))))

(defn block-values [board coord]
  (let [[r c] coord
        top (* 3 (int (/ r 3)))
        left (* 3 (int (/ c 3)))
        all-coord (apply concat (for [x (range 3)] (for [y (range 3)] [(+ top (+ 0 x)) (+ left (+ 0 y))])))]
    (set (distinct (for [[x y] all-coord] (value-at board [x y]))))))

(defn valid-values-for [board coord]
  (cond
    (has-value? board coord) #{}
    :else (clojure.set/difference
           (clojure.set/difference
            (clojure.set/difference all-values (block-values board coord))
            (row-values board coord))
           (col-values board coord))))

(defn filled? [board]
  (not (contains? (set (flatten board)) 0)))

(defn rows [board]
  (for [x (range 9)] (row-values board [x 0])))


(defn cols [board]
  (for [x (range 9)] (col-values board [0 x])))

(defn blocks [board]
  (let [all-coord (for [[x y] (apply concat (for [x (range 3)] (for [y (range 3)] [(* 3 x) (* 3 y)])))] [x y])]
    (for [[x y] all-coord] (block-values board [x y]))))

(defn valid-rows? [board]
  (every? true? (map (fn [n] (= (set n) all-values)) (rows board))))

(defn valid-cols? [board]
  (every? true? (map (fn [n] (= (set n) all-values)) (cols board))))

(defn valid-blocks? [board]
  (every? true? (map (fn [n] (= (set n) all-values)) (blocks board))))

(defn valid-solution? [board]
  (every? true? [(valid-blocks? board) (valid-rows? board) (valid-cols? board)]))

(defn set-value-at [board coord new-value]
  (assoc-in board coord new-value))

(defn find-empty-point-helper [board r c]
  (loop [board board
         r r
         c c]
    (cond
      (= 9 r) '()
      (not (has-value? board [r c])) [r c]
      :else (recur
             board
             (cond
               (= c 9) (+ 1 r)
               :else r)
             (cond
               (= c 9) 0
               :else (+ 1 c))))))

(defn find-empty-point [board]
  (find-empty-point-helper board 0 0))

(defn solve [board]
  (cond
    (valid-solution? board) board
    (filled? board) nil
    :else (let [coord (find-empty-point board)]
            (apply concat (for [x (valid-values-for board coord)] (solve (set-value-at board coord x)))))))

;************************************************************************************************************************************************************

;RED ID- 818479786
;Name: Ashwini Ramesh More
;Assignment No. 5

;************************************************************************************************************************************************************

(ns videostore.database
(:gen-class)
(:require[seesaw.core :as seesaw]
         [seesaw.mig :as mig]
         [clojure.java.io :as io]))

;------------------------------------------------------------------------------------------------------------------------------
(defn get_records "Returns the records from a file"
 [filename]
 (loop [cnt (with-open [rdr (io/reader filename)]
             (count (line-seq rdr)))
        ans {}]
  (if (= cnt 0) (into [] ans)
  (recur (dec cnt)
   (cons (read-string(with-open [rdr (io/reader filename)]
   (nth (line-seq rdr) (dec cnt)))) ans)))))

;------------------------------------------------------------------------------------------------------------------------------
(defn write_to_movie_file
 [value]
 (with-open [w (io/writer "movie.txt" :append true)]
  (binding [*out* w]
  (prn value))))

;------------------------------------------------------------------------------------------------------------------------------
(defn update_movie_file
 [table cnt index]
 (write_to_movie_file (seesaw.table/value-at table index))
  (if (< index cnt)
  (recur table cnt (inc index))))

;------------------------------------------------------------------------------------------------------------------------------
(defn- read_char_from_file
 [r]
 (try
  (read r)
 (catch java.lang.RuntimeException e
  (if (= "EOF while reading" (.getMessage e))
   ::EOF
  (throw e)))))

;------------------------------------------------------------------------------------------------------------------------------
(defn read_file
 [filename]
 (with-open [r (java.io.PushbackReader. (io/reader filename))]
  (binding [*read-eval* false]
  (doall (take-while #(not= ::EOF %) (repeatedly #(read_char_from_file r)))))))

;------------------------------------------------------------------------------------------------------------------------------
(defn get_list "Returns all the values for the corresponding key from file"
 [key filename]
 (into []
  (map key (into [] (read_file filename)))))

;------------------------------------------------------------------------------------------------------------------------------
(defn getDueDate "Calculate Due date which is 2 weeks from current date"
 []
 (let [cal (java.util.Calendar/getInstance)]
  (.add cal java.util.Calendar/DAY_OF_YEAR 14)
  (.format (java.text.SimpleDateFormat. "MMM.dd.yyyy")
 (.getTime cal))))

;------------------------------------------------------------------------------------------------------------------------------
(defn getId "Return the Id of the corresponding index/record"
 [index]
 (:ID (read-string(with-open [rdr (io/reader "movie.txt")]
  (nth (line-seq rdr) index)))))

;------------------------------------------------------------------------------------------------------------------------------
(defn getMoviename "Return the MovieName of the corresponding index/record"
 [index]
 (str(:MovieName (read-string(with-open [rdr (io/reader "movie.txt")]
   (nth (line-seq rdr) index))))))

;------------------------------------------------------------------------------------------------------------------------------
(defn getQty "Return the Quantity of the corresponding index/record"
 [index]
 (:Quantity (read-string(with-open [rdr (io/reader "movie.txt")]
   (nth (line-seq rdr) index)))))

;------------------------------------------------------------------------------------------------------------------------------
(defn getPrice "Return the Price of the corresponding index/record"
 [index]
 (:Price (read-string(with-open [rdr (io/reader "movie.txt")]
  (nth (line-seq rdr) index)))))

;------------------------------------------------------------------------------------------------------------------------------
(defn write_to_renter_file
 [value]
 (with-open [w (io/writer "renter.txt" :append true)]
  (binding [*out* w]
  (prn value))))

;------------------------------------------------------------------------------------------------------------------------------
;ADD MOVIE
;------------------------------------------------------------------------------------------------------------------------------
(defn add_to_movie_file "Insert the new record in movie database file"
 [movie_id name qty price]
 (let [new_id (inc(with-open [rdr (io/reader "movie.txt")]
               (count (line-seq rdr))))]
 (seesaw/text! movie_id new_id)
 (write_to_movie_file {:ID new_id :MovieName name :Quantity qty :Price price}))
 (seesaw/alert "Movie added successfully!"))

;------------------------------------------------------------------------------------------------------------------------------
;ADD COPY OF EXISTING MOVIE
;------------------------------------------------------------------------------------------------------------------------------
(defn add_copy_handler "Increment the quantity of the selected record and update movie database file"
 [table]
 (let [index (seesaw/selection table)
       qty (str(inc
            (read-string(:Quantity (seesaw.table/value-at table index)))))
       cnt (dec(seesaw.table/row-count table))]
 (seesaw.table/update-at! table index [(getId index) (getMoviename index) qty (getPrice index)])
 (clojure.java.io/delete-file "movie.txt")
 (update_movie_file table cnt 0))
 (seesaw/alert "Movie copy added successfully!!"))

;------------------------------------------------------------------------------------------------------------------------------
;RENT A MOVIE
;------------------------------------------------------------------------------------------------------------------------------
(defn update_renter_data "Insert in renter database file and update available-movies-table and also movie database file"
 [renter_name movie_name quantity val_price due-date]
 (write_to_renter_file {:RenterName renter_name :MovieName movie_name :Quantity quantity :Price val_price :Due-date due-date})
 (let [table (seesaw/table :model [:columns [:ID :MovieName :Quantity :Price] :rows (get_records "movie.txt")]
                           :font "ARIAL-BOLD" :foreground "purple" :background "white")
       list_of_movies (get_list :MovieName "movie.txt")
       index (.indexOf list_of_movies movie_name)
       id (:ID (seesaw.table/value-at table index))
       qty (str(dec
            (read-string(:Quantity (seesaw.table/value-at table index)))))
       price (:Price (seesaw.table/value-at table index))
       cnt (dec(seesaw.table/row-count table))]
  (seesaw.table/update-at! table index [id movie_name qty price])
  (clojure.java.io/delete-file "movie.txt")
	(update_movie_file table cnt 0))
  (seesaw/alert "Movie Rented successfully!"))

;------------------------------------------------------------------------------------------------------------------------------
(defn check_availability "Check if movie is available for rent"
 [listmovie listbox renter_name quantity text_price due-date]
 (let [selected_value (seesaw/selection listbox)
       index (.indexOf listmovie selected_value)
       val_price (getPrice index)
       qty (read-string(getQty index))]
  (seesaw/text! text_price val_price)
  (if (zero? qty)
   (seesaw/alert "Sorry, Movie not available to rent!!")
   (update_renter_data renter_name selected_value quantity val_price due-date))))

;------------------------------------------------------------------------------------------------------------------------------
;UPDATE QUANTITY
;------------------------------------------------------------------------------------------------------------------------------
(defn update_qty_handler "Update the quantity of the selected record and update movie database file"
 [table]
 (let [input (seesaw/input "Enter new Quantity : ")
       index (seesaw/selection table)
       cnt (dec(seesaw.table/row-count table))]
 (seesaw.table/update-at! table index [(getId index) (getMoviename index) input (getPrice index)])
 (clojure.java.io/delete-file "movie.txt")
 (update_movie_file table cnt 0)))

;------------------------------------------------------------------------------------------------------------------------------
;UPDATE PRICE
;------------------------------------------------------------------------------------------------------------------------------
(defn update_price_handler "Update the price of the selected record and update movie database file"
 [table]
 (let [input (seesaw/input "Enter new Price : ")
       index (seesaw/selection table)
       cnt (dec(seesaw.table/row-count table))]
 (seesaw.table/update-at! table index [(getId index) (getMoviename index) (getQty index) input])
 (clojure.java.io/delete-file "movie.txt")
 (update_movie_file table cnt 0)))

;------------------------------------------------------------------------------------------------------------------------------
;REMOVE COPY OF A MOVIE
;------------------------------------------------------------------------------------------------------------------------------
(defn remove_copy_handler "Decrement the quantity of the selected record and update movie database file"
 [table index qty]
 (if (zero? qty)
  (seesaw/alert "Quantity is already ZERO!!")
  (let [quantity (str(dec
                  (read-string(:Quantity (seesaw.table/value-at table index)))))
        cnt (dec(seesaw.table/row-count table))]
  (seesaw.table/update-at! table index[(getId index) (getMoviename index) quantity (getPrice index)])
  (clojure.java.io/delete-file "movie.txt")
  (update_movie_file table cnt 0)
  (seesaw/alert "Movie copy removed successfully!!"))))

;------------------------------------------------------------------------------------------------------------------------------
(defn get_quantity "Returns the quantity of the selected row"
 [table]
 (let [index (seesaw/selection table)
       qty (read-string(:Quantity (seesaw.table/value-at table index)))]
 (remove_copy_handler table index qty)))

;------------------------------------------------------------------------------------------------------------------------------
;DISPLAY LIST OF RENTED MOVIES
;------------------------------------------------------------------------------------------------------------------------------
(defn make_available "Update the quantity of a record in available-movies-table and update movie database file"
 [movie_name]
  (let [table (seesaw/table :model [:columns [:ID :MovieName :Quantity :Price] :rows (get_records "movie.txt")]
                           :font "ARIAL-BOLD" :foreground "purple" :background "white")
        list_of_movies (get_list :MovieName "movie.txt")
        index (.indexOf list_of_movies movie_name)
        id (:ID (seesaw.table/value-at table index))
        qty (str(inc
             (read-string(:Quantity (seesaw.table/value-at table index)))))
        price (:Price(seesaw.table/value-at table index))
        cnt (dec(seesaw.table/row-count table))]
  (seesaw.table/update-at! table index [id movie_name qty price])
  (clojure.java.io/delete-file "movie.txt")
	(update_movie_file table cnt 0)))

;------------------------------------------------------------------------------------------------------------------------------
(defn update_renter_file "Update the new changes to the renter database file"
 [table cnt index]
  (write_to_renter_file (seesaw.table/value-at table index))
  (if (< index cnt)
   (recur table cnt (inc index))))

;------------------------------------------------------------------------------------------------------------------------------
(defn remove_record "Remove the record from rented-movies-table and update renter database file"
 [rent_table filename]
 (let [index (seesaw/selection rent_table)
       movie_name (:MovieName(read-string(with-open [rdr (io/reader "renter.txt")]
                             (nth (line-seq rdr) index))))
       cnt (dec(seesaw.table/row-count rent_table))]
  (seesaw.table/remove-at! rent_table index)
  (if (zero? cnt)
   (spit "renter.txt" "" :overwrite true)
   (clojure.java.io/delete-file "renter.txt"))
  (if (pos? cnt)
  (update_renter_file rent_table (dec cnt) 0))
  (make_available movie_name))
  (seesaw/alert "Movie made available successfully!!"))

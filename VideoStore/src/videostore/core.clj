;************************************************************************************************************************************************************

;RED ID- 818479786
;Name: Ashwini Ramesh More
;Assignment No. 5

;************************************************************************************************************************************************************
(ns videostore.core
 (:gen-class)
 (:require [videostore.database :refer :all]
           [seesaw.core :as seesaw]
           [seesaw.mig :as mig]
           [clojure.java.io :as io]))

;------------------------------------------------------------------------------------------------------------------------------
;ADD MOVIE
;------------------------------------------------------------------------------------------------------------------------------
(defn add_movie
 [event]
 (seesaw/dispose! (seesaw/to-root event))
 (let [id (inc (with-open [rdr (io/reader "movie.txt")]
           (count (line-seq rdr))))
       movie_id (seesaw/text :text id :editable? false :columns 10 :foreground "purple")
       movie_name (seesaw/text :text "Name" :columns 10)
       quantity (seesaw/text :text "1" :columns 10)
       price (seesaw/text :text "10" :columns 10)
       add_button (seesaw/button :text "Add Movie" :foreground "purple"
                   :listen [:action (fn[e]
                   (add_to_movie_file movie_id (seesaw/text movie_name) (seesaw/text quantity) (seesaw/text price)))])]
(seesaw/top-bottom-split
 (seesaw/grid-panel :border "Add Movie" :columns 2 :foreground "purple" :background "white"
 :items [(seesaw/label :text "Movie ID : " :foreground "purple") movie_id
         (seesaw/label :text "Enter Movie Name : " :foreground "purple") movie_name
         (seesaw/label :text "Enter Quantity : " :foreground "purple") quantity
         (seesaw/label :text "Enter Rental Price : " :foreground "purple") price])
 (mig/mig-panel :constraints ["wrap 3" "[70%]30[70%]"] :items[[" "] [add_button] [" "]])
 :divider-location 150)))

;------------------------------------------------------------------------------------------------------------------------------
;ADD COPY OF EXISTING MOVIE
;------------------------------------------------------------------------------------------------------------------------------
(defn add_copy
 [event]
 (seesaw/dispose! (seesaw/to-root event))
 (let [table (seesaw/table :model [:columns [:ID :MovieName :Quantity :Price] :rows (get_records "movie.txt")]
                        :font "ARIAL-BOLD" :foreground "purple" :background "white")
       add_button (seesaw/button :text "Add a copy" :foreground "purple"
                   :listen [:action (fn[e] (add_copy_handler table))])]
  (seesaw/top-bottom-split
   (seesaw/grid-panel :border "Add a copy" :columns 1 :foreground "purple" :background "white" :items [(seesaw/scrollable table)])
   (mig/mig-panel :constraints ["wrap 3" "[70%]30[70%]"] :items[[" "] [add_button] [" "]])
  :divider-location 150)))

;------------------------------------------------------------------------------------------------------------------------------
;RENT A MOVIE
;------------------------------------------------------------------------------------------------------------------------------
(defn rent_movie
 [event]
 (seesaw/dispose! (seesaw/to-root event))
 (let [renter_name (seesaw/text :text "Name" :columns 10)
       listmovie (get_list :MovieName "movie.txt")
       listbox (seesaw/listbox :model (get_list :MovieName "movie.txt"))
       quantity (seesaw/text :text "1" :columns 10 :editable? false)
       price (seesaw/text :text " " :columns 10 :font "ARIAL-BOLD" :editable? false)
       due-date (seesaw/text :text (getDueDate) :columns 10 :editable? false)
       add_button (seesaw/button :text "Rent a Movie" :foreground "purple"
                   :listen [:action (fn[e] (check_availability listmovie listbox
                   (seesaw/text renter_name) (seesaw/text quantity) price (seesaw/text due-date)))])]
 (seesaw/top-bottom-split
  (seesaw/grid-panel :border "Rent a Movie" :columns 2 :foreground "purple" :background "white"
   :items [(seesaw/label :text "Enter Renter's Name : " :foreground "purple") renter_name
          (seesaw/label :text "Select Movie : " :foreground "purple") (seesaw/scrollable listbox)
          (seesaw/label :text "Quantity : " :foreground "purple") quantity
          (seesaw/label :text "Rental Price : " :foreground "purple") price
          (seesaw/label :text "Due Date : " :foreground "purple") due-date])
   (mig/mig-panel :constraints ["wrap 3" "[70%]30[70%]"] :items[[" "] [(seesaw/border-panel :center add_button)][" "]])
  :divider-location 150)))

;------------------------------------------------------------------------------------------------------------------------------
;UPDATE QUANTITY
;------------------------------------------------------------------------------------------------------------------------------
(defn update_qty
 [event]
 (seesaw/dispose! (seesaw/to-root event))
 (let [table (seesaw/table :model [:columns [:ID :MovieName :Quantity :Price] :rows (get_records "movie.txt")]
                           :font "ARIAL-BOLD" :foreground "purple" :background "white")
       updateQty_button (seesaw/button :text "Update Quantity" :foreground "purple"
                        :listen [:action (fn[e] (update_qty_handler table))])]
  (seesaw/top-bottom-split
   (seesaw/grid-panel :border "Available Movies" :columns 1 :foreground "purple" :background "white" :items [(seesaw/scrollable table)])
   (mig/mig-panel :constraints ["wrap 3" "[70%]30[70%]"] :items[[" "] [updateQty_button] [" "]])
  :divider-location 150)))

;------------------------------------------------------------------------------------------------------------------------------
;UPDATE PRICE
;------------------------------------------------------------------------------------------------------------------------------
(defn update_price
 [event]
 (seesaw/dispose! (seesaw/to-root event))
 (let [table (seesaw/table :model [:columns [:ID :MovieName :Quantity :Price] :rows (get_records "movie.txt")]
                           :font "ARIAL-BOLD" :foreground "purple" :background "white")
       updatePrice_button (seesaw/button :text "Update Price" :foreground "purple"
                          :listen [:action (fn[e] (update_price_handler table))])]
  (seesaw/top-bottom-split
   (seesaw/grid-panel :border "Available Movies" :columns 1 :foreground "purple" :background "white" :items [(seesaw/scrollable table)])
   (mig/mig-panel :constraints ["wrap 3" "[70%]30[70%]"] :items[[" "] [updatePrice_button] [" "]])
  :divider-location 150)))

;------------------------------------------------------------------------------------------------------------------------------
;REMOVE COPY OF A MOVIE
;------------------------------------------------------------------------------------------------------------------------------
(defn remove_copy
 [event]
 (seesaw/dispose! (seesaw/to-root event))
 (let [table (seesaw/table :model [:columns [:ID :MovieName :Quantity :Price] :rows (get_records "movie.txt")]
                           :font "ARIAL-BOLD" :foreground "purple" :background "white")
       remove_button (seesaw/button :text "Remove a copy" :foreground "purple"
                     :listen [:action (fn[e] (get_quantity table))])]
  (seesaw/top-bottom-split
   (seesaw/grid-panel :border "Remove a copy" :columns 1 :foreground "purple" :background "white" :items [(seesaw/scrollable table)])
   (mig/mig-panel :constraints ["wrap 3" "[70%]30[70%]"] :items[[" "] [remove_button] [" "]])
  :divider-location 150)))

;------------------------------------------------------------------------------------------------------------------------------
;SEARCH A MOVIE
;------------------------------------------------------------------------------------------------------------------------------
(defn show "Output the quantity and price of the selected Id/Moviename"
 [list table]
 (let [val (seesaw/selection table)
       index (.indexOf list val)
       content (seesaw/grid-panel :columns 2 :foreground "purple" :background "white"
                :items [(seesaw/label :text "Quantity :-" :foreground "purple")
                        (seesaw/text :text (getQty index) :columns 5 :editable? false)
                        (seesaw/label :text " Price :-" :foreground "purple")
                        (seesaw/text :text (getPrice index) :columns 5 :editable? false)])
       ok_button (seesaw/button :text "OK" :foreground "purple" :listen[:action(fn[e] (seesaw/dispose! (seesaw/to-root e)))])]
 (-> (seesaw/frame :title "Video Store"
     :content (seesaw/top-bottom-split content
              (mig/mig-panel :constraints ["wrap 3" "[70%]30[70%]"]
              :items[[" "] [ok_button] [" "]]) :divider-location 50)
     :width 140 :height 150)
  (seesaw/show!)
  (seesaw/move! :by [550 320]))))

;------------------------------------------------------------------------------------------------------------------------------
(defn search_movie
 [event]
 (seesaw/dispose! (seesaw/to-root event))
 (let [listid (get_list :ID "movie.txt")
      listmovie (get_list :MovieName "movie.txt")
      movie_id (seesaw/listbox :model (get_list :ID "movie.txt") )
      movie_name (seesaw/listbox :model (get_list :MovieName "movie.txt") )
      searchid_button (seesaw/button :text "Search" :foreground "purple"
                       :listen [:action (fn[e] (show listid movie_id))])
      searchname_button (seesaw/button :text "Search" :foreground "purple"
                         :listen [:action (fn[e] (show listmovie movie_name))])]
 (seesaw/top-bottom-split
  (seesaw/top-bottom-split
   (seesaw/grid-panel :border "Search by ID" :columns 2 :foreground "purple" :background "white"
    :items [(seesaw/label :text "Select ID : " :foreground "purple") (seesaw/scrollable movie_id)])
    (mig/mig-panel :constraints ["wrap 3" "[70%]30[70%]"] :items[[" "] [searchid_button] [" "]]))
  (seesaw/top-bottom-split
    (seesaw/grid-panel :border "Search by MovieName" :columns 2 :foreground "purple" :background "white"
     :items [(seesaw/label :text "Select MovieName : " :foreground "purple") (seesaw/scrollable movie_name)])
    (mig/mig-panel :constraints ["wrap 3" "[70%]30[70%]"] :items[[" "] [searchname_button] [" "]])))))

;------------------------------------------------------------------------------------------------------------------------------
;DISPLAY LIST OF AVAILABLE MOVIES
;------------------------------------------------------------------------------------------------------------------------------
(defn list_available_movies
 [event]
 (seesaw/dispose! (seesaw/to-root event))
 (let [table (seesaw/table :model [:columns [:ID :MovieName :Quantity :Price] :rows (get_records "movie.txt")]
                           :font "ARIAL-BOLD" :foreground "purple" :background "white")
       move_button (seesaw/button :text "Available Now!!" :foreground "purple"
                   :listen [:action (fn[e] (remove_record table "renter.txt"))])]
  (seesaw/top-bottom-split
   (seesaw/grid-panel :border "Available Movies" :columns 1 :foreground "purple" :background "white"
    :items [(seesaw/scrollable table)])
   (mig/mig-panel :constraints ["wrap 3" "[70%]30[70%]"] :items[[" "] [move_button] [" "]])
   :divider-location 200)))

;------------------------------------------------------------------------------------------------------------------------------
;DISPLAY LIST OF RENTED MOVIES
;------------------------------------------------------------------------------------------------------------------------------
(defn list_rented_movies
 [event]
 (seesaw/dispose! (seesaw/to-root event))
 (let [table (seesaw/table :model [:columns [:RenterName :MovieName :Quantity :Price :Due-date] :rows (get_records "renter.txt")]
                        :font "ARIAL-BOLD" :foreground "purple" :background "white")
       move_button (seesaw/button :text "Available Now!!" :foreground "purple"
                   :listen [:action (fn[e] (remove_record table "renter.txt"))])]
  (seesaw/top-bottom-split
   (seesaw/grid-panel :border "Rented Movies" :columns 1 :foreground "purple" :background "white"
    :items [(seesaw/scrollable table)])
   (mig/mig-panel :constraints ["wrap 3" "[70%]30[70%]"] :items[[" "] [move_button] [" "]])
   :divider-location 150)))

;------------------------------------------------------------------------------------------------------------------------------
;DISPLAY MENUS
;------------------------------------------------------------------------------------------------------------------------------
(declare display)

(def menu
 (seesaw/menubar :background "purple"
  :items [(seesaw/menu :text "Add" :foreground "white"
 :items [(seesaw/action :name "Add New Movie"
 :key "menu A"
 :handler (fn[event] (display (add_movie event))))
(seesaw/action :name "Add a copy to existing movie"
 :key "menu C"
 :handler (fn[event] (display (add_copy event))))])
(seesaw/menu :text "Rent" :foreground "white"
 :items [(seesaw/action :name "Rent a Movie"
 :key "menu R"
 :handler (fn[event] (display (rent_movie event))))])
(seesaw/menu :text "Update" :foreground "white"
 :items [(seesaw/action :name " Change Quantity"
 :key "menu Q"
 :handler (fn[event] (display (update_qty event))))
(seesaw/action :name "Change Price"
 :key "menu P"
 :handler (fn[event] (display (update_price event))))])
(seesaw/menu :text "Remove" :foreground "white"
 :items [(seesaw/action :name "Remove a copy"
 :key "menu M"
 :handler (fn[event] (display (remove_copy event))))])
(seesaw/menu :text "Search" :foreground "white"
 :items [(seesaw/action :name "Search a Movie"
 :key "menu S"
 :handler (fn[event] (display (search_movie event))))])
(seesaw/menu :text "View" :foreground "white"
 :items [(seesaw/action :name "Available Movies"
 :key "menu V"
 :handler (fn[event] (display (list_available_movies event))))
(seesaw/action :name "Rented Movies"
 :key "menu W"
 :handler (fn[event] (display (list_rented_movies event))))])
(seesaw/menu :text "Exit" :foreground "white"
 :items [(seesaw/action :name "Exit"
 :key "menu E"
 :handler (fn[event] (seesaw/dispose! (seesaw/to-root event))))])]))

;------------------------------------------------------------------------------------------------------------------------------
(defn display
 [content]
 (let [window (seesaw/frame :title "Video Store" :menubar menu
  :content content
  :width 400
  :height 270)]
  (seesaw/show! window)
  (seesaw/move! window :by [434 250])))

;------------------------------------------------------------------------------------------------------------------------------
(def home
 (seesaw/border-panel :center
   (seesaw/label :text "Welcome to SDSU Video Store" :foreground "purple" :background "white"
	 :font "ARIAL-BOLD-25" :halign :center)))

;------------------------------------------------------------------------------------------------------------------------------
(defn -main
 [& args]
 (display home))

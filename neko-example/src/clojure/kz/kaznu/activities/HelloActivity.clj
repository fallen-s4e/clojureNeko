(ns kz.kaznu.activities.HelloActivity
  (:import [kz.kaznu.activities R$layout])
  (:import [kz.kaznu.activities R$id])
  (:require neko.compilation)
  (:use clojure.core)
  (:use kz.kaznu.base.client)
  (:use kz.kaznu.base.utils)
  (:use kz.kaznu.base.serialization)
  (:gen-class :main false
              :extends android.app.Activity
              :exposes-methods {onCreate superOnCreate}))

(def max-result-len 150)

;; components accessing

(defn init-components[ this ]
  (def ui-run-button      (.findViewById this R$id/runButton))
  (def ui-clear-button    (.findViewById this R$id/clearButton))
  (def ui-input-edit-text (.findViewById this R$id/inputEditText))
  (def ui-input-address   (.findViewById this R$id/inputAddress)))

;; utility methods

(defn set-result [this string-value]
  (.setText (.findViewById this R$id/resultView)
            (if (> (.length string-value) max-result-len)
              (.substring string-value 0 max-result-len)
              string-value)))

(defn set-on-click[button fun]
  (.setOnClickListener button
                       (proxy [ android.view.View$OnClickListener ]
                           []
                         (onClick [#^android.view.View view] (fun view)))))

;; eval methods

(defn eval-new-thread
  "evaluates expressions in new thread, catching all the errors being in this namespace kz.kaznu.activities.HelloActivity"
  [ & exprs ]
  @(future
     (binding [*ns* (find-ns 'kz.kaznu.activities.HelloActivity)]
       ((kz.kaznu.base.utils/catch-all apply)
        eval exprs))))

(defn eval-remote
  "evaluates expressions in new thread, catching all the errors being in this namespace kz.kaznu.activities.HelloActivity"
  [ expr input-addr ]
  @(future
     ((kz.kaznu.base.utils/catch-all kz.kaznu.base.client/runr)
      input-addr expr)))

;; initializing

(defn init-buttons-actions[this]
  (set-on-click ui-run-button (kz.kaznu.base.utils/catch-all
                               (fn[_]
                                 (let [input      (read-string (.toString (.getText ui-input-edit-text)))
                                       input-addr (.toString (.getText ui-input-address))]
                                   (try
                                     (set-result this "waiting ...")
                                     (if (or (nil? input-addr) (= input-addr ""))
                                       (set-result this (str (eval-new-thread input)))
                                       (set-result this (str (eval-remote input input-addr))))
                                     (catch Exception ex
                                       (set-result this (str "error occured:" ex))))))))
  (set-on-click ui-clear-button (fn[_]
                                  (.setText ui-input-edit-text "")
                                  (set-result this ""))))

;; overridings
(defn -onCreate
  [this bundle]
  (doto this
    (.superOnCreate bundle)
    (.setContentView R$layout/main))
  (init-components this)
  (init-buttons-actions this)
  (neko.compilation/init this) ;; allowing dynamic compilation
  )

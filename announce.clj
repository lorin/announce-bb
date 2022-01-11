#!/usr/bin/env bb
(ns announce
  (:require [clojure.string :refer [trim]])
  (:require [clojure.java.shell :refer [sh]]))

(def usage "Usage:

    announce on
    announce off")


  
(def announce-the-time-domain "com.apple.speech.synthesis.general.prefs")
(def announce-the-time-key "TimeAnnouncementPrefs")
(def announce-the-time-enabled-key "TimeAnnouncementsEnabled")

(defn set-boolean-pref
"Set a boolean nested preference"
  [domain-name key nested-key flag]
    (let [domain (str (System/getProperty "home") "/Library/Preferences/" domain-name)]
    (sh "defaults" "write" domain key "-dict-add" nested-key "-bool" (if flag "YES" "NO"))))

(def set-announce-the-time-flag (partial set-boolean-pref announce-the-time-domain announce-the-time-key announce-the-time-enabled-key))


(defn start-service
  [service-name]
  (let [uid (trim (:out (sh "id" "-u")))
        service (str "gui/" uid "/" service-name)]
    (sh "launchctl" "kickstart" service)))
  
(defn stop-service
[service-name]  
  (let [uid (trim (:out (sh "id" "-u")))
        service (str "gui/" uid "/" service-name)]
    (sh "launchctl" "kill" "SIGTERM" service)))
  
(def speech-synthesis-server-name "com.apple.speech.synthesisserver")


(defn turn-on
  []
  (set-announce-the-time-flag true)
  (start-service speech-synthesis-server-name))

(defn turn-off
  []
  (set-announce-the-time-flag false)
  (stop-service speech-synthesis-server-name))
  
(defn print-usage
  []
  (println usage))

(defn main
  [args]
  (if-let [[arg] args]
    (case arg
      "on" (turn-on)
      "off" (turn-off)
      (print-usage))
    (print-usage)))

(main *command-line-args*)
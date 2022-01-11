#!/usr/bin/env bb
(ns announce
  (:require [clojure.string :refer [trim]])
  (:require [clojure.java.shell :refer [sh]]))

(def usage "Usage:

    announce on
    announce off")


(defn set-boolean-pref
"Set a boolean nested preference"
  [domain-name key nested-key flag]
    (let [domain (str (System/getProperty "home") "/Library/Preferences/" domain-name)]
    (sh "defaults" "write" domain key "-dict-add" nested-key "-bool" (if flag "YES" "NO"))))
  
(def announce-the-time-domain "com.apple.speech.synthesis.general.prefs")
(def announce-the-time-key "TimeAnnouncementPrefs")
(def announce-the-time-enabled-key "TimeAnnouncementsEnabled")
(def set-or-unset-announce-the-time-flag (partial set-boolean-pref announce-the-time-domain announce-the-time-key announce-the-time-enabled-key))


(def set-announce-the-time-pref #(set-or-unset-announce-the-time-flag true))
(def unset-announce-the-time-pref #(set-or-unset-announce-the-time-flag true))

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
  
(defn start-speech-synthesis-server
  []
  (start-service "com.apple.speech.synthesisserver"))

(defn stop-speech-synthesis-server
  []
  (stop-service "com.apple.speech.synthesisserver"))

(defn turn-on
  []
  (set-announce-the-time-pref)
  (start-speech-synthesis-server))

(defn turn-off
  []
  (unset-announce-the-time-pref)
  (stop-speech-synthesis-server))
  
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
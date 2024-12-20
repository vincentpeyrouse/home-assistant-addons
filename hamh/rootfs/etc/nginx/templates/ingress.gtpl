server {
    listen 8099 default_server;

    root            /dev/null;
    server_name     $hostname;

    # server params
    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";
    add_header X-Robots-Tag none;

    # proxy params
    proxy_http_version          1.1;
    proxy_ignore_client_abort   off;
    proxy_read_timeout          86400s;
    proxy_redirect              off;
    proxy_send_timeout          86400s;
    proxy_max_temp_file_size    0;

    proxy_set_header Accept-Encoding "";
    proxy_set_header Connection $connection_upgrade;
    proxy_set_header Host $http_host;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-NginX-Proxy true;
    proxy_set_header X-Real-IP $remote_addr;

    proxy_set_header            X-Forwarded-Uri        $http_x_ingress_path;
    proxy_set_header            X-SSL                  true;

    # avoid mixed content
    # Ensure work with both http and https (code by @met67)
    if ($http_referer ~* "^(http[s]?)://([^:]+):(\d*)(/.*)$") {
      set $x_scheme $1;
      set $x_host   $2;
      set $x_port   ":$3";
    }
    # Ensure works if standard port (code by @met67)
    if ($http_referer ~* "^(http[s]?)://([^:]+)(/.*)$") {
      set $x_scheme $1;
      set $x_host   $2;
      set $x_port   "";
    }
    proxy_set_header X-Scheme          $x_scheme;
    proxy_set_header X-Forwarded-Proto $x_scheme;
    
    # Correct url without port when using https
    sub_filter_once off;
    sub_filter_types *;
    sub_filter          https://$host/          $x_scheme://$host$x_port/;
    sub_filter          http://$host/           $x_scheme://$host$x_port/;
    sub_filter          /assets                 $http_x_ingress_path/assets;
    sub_filter          /api/matter             $http_x_ingress_path/api/matter;

    rewrite ^(.*)/api/hassio_ingress/.*(/api/hassio_ingress/.*)$ $1$2 permanent;

    location / {
        allow   172.30.32.2;
        deny    all;
        proxy_pass http://127.0.0.1:{{ .port }};
    }

}
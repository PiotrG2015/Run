<?php
return new \Phalcon\Config(array(
	'database' => array(
		'adapter'  => 'Mysql',
		'host'     => 'localhost',
		'username' => 'username',
		'password' => 'password',
		'name'     => 'dbname',
	),
	'application' => array(
		'modelsDir'      => __DIR__ . '/../models/',
		'baseUri'        => '/api/',
	),
	'models' => array(
		'metadata' => array(
			'adapter' => 'Memory'
		)
	)
));
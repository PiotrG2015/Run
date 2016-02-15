<?php

use Phalcon\Mvc\Model;
use Phalcon\Mvc\Model\Message;
use Phalcon\Mvc\Model\Validator\Uniqueness;
use Phalcon\Mvc\Model\Validator\InclusionIn;

class Messages extends Model
{
    public $content;
	public $mid;
    public $sender_id;
	public $receiver_id;
    public $is_new;
    public $created_at;
	
}
?>